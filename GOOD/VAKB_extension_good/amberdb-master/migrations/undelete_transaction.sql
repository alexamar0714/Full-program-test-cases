-- ===================================
-- Indexes to make un-delete run faster
-- ===================================
CREATE INDEX acknowledge_history_txn_end ON acknowledge_history (txn_end);
CREATE INDEX description_history_txn_end ON description_history (txn_end);
CREATE INDEX file_history_txn_end ON file_history (txn_end);
CREATE INDEX node_history_txn_end ON node_history (txn_end);
CREATE INDEX party_history_txn_end ON party_history (txn_end);
CREATE INDEX tag_history_txn_end ON tag_history (txn_end);
CREATE INDEX work_history_txn_end ON work_history (txn_end);
CREATE INDEX flatedge_history_txn_end ON flatedge_history (txn_end);


-- =============================================
-- Un-delete transaction nodes
--
-- Also prepares Works and Copies for reindexing
-- =============================================
DROP PROCEDURE IF EXISTS undelete_node;
DELIMITER //
CREATE PROCEDURE undelete_node(IN txnId BIGINT)

  BEGIN

    -- 1) update the index staging table to reindex Works and Copies (barring Closed Works)
    INSERT INTO txni_staging
      SELECT id, 'I', NULL FROM node_history h
      WHERE txn_end = txnId
      AND NOT EXISTS (SELECT 1 FROM node n WHERE n.id = h.id)
      AND h.type IN ('Work', 'Page', 'Section', 'EADWork', 'Copy')
      AND h.internalAccessConditions <> 'Closed';


    -- 2) insert deleted nodes into active table
    --    NOTE: deleted nodes won't exist in active table, but modified ones will, so check
    INSERT INTO node
      SELECT * FROM node_history h
      WHERE txn_end = txnId
      AND NOT EXISTS (SELECT 1 FROM node n WHERE n.id = h.id);


    -- 3) update the txn_end in the active table
    UPDATE node SET txn_end = 0 WHERE txn_end = txnId;


    -- 4) update the txn_end in the history table
    --    NOTE: deleted nodes should now exist in the active table and match txn_start
    UPDATE node_history h SET txn_end = 0
    WHERE txn_end = txnId
    AND EXISTS (SELECT 1 FROM node n WHERE n.id = h.id AND n.txn_end = 0 AND n.txn_start = h.txn_start);

  END //
DELIMITER ;


-- =====================================
-- Un-delete flat table objects
--
-- Generic flat table object un-deletion
-- =====================================
DROP PROCEDURE IF EXISTS undelete_flat_table;
DELIMITER //
CREATE PROCEDURE undelete_flat_table(IN txnId BIGINT, IN tableName CHAR(50))

  BEGIN

    SET @table_name = tableName;


    -- 1) insert deleted objects into active table
    --    NOTE: deleted objects won't exist in active table, but modified ones will, so check
    SET @insert_stmt = CONCAT(' INSERT INTO ', @table_name,
                              ' SELECT * FROM ', @table_name, '_history h ',
                              ' WHERE txn_end = ', txnId,
                              ' AND NOT EXISTS',
                              '   (SELECT 1 FROM ', @table_name, ' t WHERE t.id = h.id)');


    -- 2) update the txn_end in the active table
    SET @update_live_stmt = CONCAT('UPDATE ', @table_name, ' SET txn_end = 0 WHERE txn_end = ', txnId);


    -- 3) update the txn_end in the history table
    --    NOTE: deleted objects should now exist in the active table and match txn_start
    SET @update_hist_stmt = CONCAT(' UPDATE ', @table_name, '_history h SET txn_end = 0 ',
                                   ' WHERE txn_end = ', txnId,
                                   ' AND EXISTS ',
                                   '   (SELECT 1 FROM ', @table_name, ' t',
                                   '    WHERE t.id = h.id AND t.txn_end = 0',
                                   '    AND t.txn_start = h.txn_start)');

    PREPARE stmt1 FROM @insert_stmt;
    PREPARE stmt2 FROM @update_live_stmt;
    PREPARE stmt3 FROM @update_hist_stmt;
    EXECUTE stmt1;
    EXECUTE stmt2;
    EXECUTE stmt3;
    DEALLOCATE PREPARE stmt1;
    DEALLOCATE PREPARE stmt2;
    DEALLOCATE PREPARE stmt3;

  END //
DELIMITER ;


-- =============================================
-- Un-delete flatedge objects
--
-- Only run after node table un-deletion so it
-- can check for broken end points
-- ============================================
DROP PROCEDURE IF EXISTS undelete_flatedge;
DELIMITER //
CREATE PROCEDURE undelete_flatedge(IN txnId BIGINT)

  BEGIN

    -- 1) insert deleted flatedges into active table
    --    NOTES: Only restore edges that have active end points
    --           Don't include edges that were modified rather than deleted
    INSERT INTO flatedge
      SELECT * FROM flatedge_history h
      WHERE h.txn_end = txnId
      AND EXISTS (SELECT 1 FROM node WHERE id = h.v_in)
      AND EXISTS (SELECT 1 FROM node WHERE id = h.v_out)
      AND NOT EXISTS (SELECT 1 FROM flatedge WHERE id = h.id);


    -- 2) update the txn_end in the active table
    UPDATE flatedge SET txn_end = 0 WHERE txn_end = txnId;


    -- 3) update the txn_end in the history table
    --    NOTE: deleted objects should now exist in the active table and match txn_start
    UPDATE flatedge_history h SET txn_end = 0
    WHERE txn_end = txnId
    AND EXISTS (SELECT 1 FROM node WHERE id = h.v_in)
    AND EXISTS (SELECT 1 FROM node WHERE id = h.v_out)
    AND EXISTS (SELECT 1 FROM flatedge f WHERE f.id = h.id AND f.txn_end = 0 AND f.txn_start = h.txn_start);

  END //
DELIMITER ;


-- ==============================================
-- Un-deletes graph table objects
--
-- This proc doesn't check whether objects were
-- only modified, so it should only be used when
-- the transaction is purely a deletion.
--
-- this proc won't be required when graph tables
-- are removed.
-- =============================================
DROP PROCEDURE IF EXISTS undelete_graph_table;
DELIMITER //
CREATE PROCEDURE undelete_graph_table(IN txnId BIGINT, IN tableName CHAR(50))

  BEGIN

    SET @table_name = tableName;

    -- simply set txn_end to 0 ** WARNING: DOES NOT CHECK IF OBJECT IS UPDATED RATHER THAN DELETED
    SET @update_stmt = CONCAT(' UPDATE ', @table_name, ' t SET t.txn_end = 0 WHERE t.txn_end = ', txnId);

    PREPARE stmt FROM @update_stmt;
    EXECUTE stmt;
    DEALLOCATE PREPARE stmt;

  END //
DELIMITER ;

-- ======================================================
-- MAIN PROCEDURE (this is the one you call)
-- ======================================================
-- Un-deletes all objects deleted in a single transaction
--
-- Wraps entire un-delete in transaction with rollback.
--
-- Unfortunately, no indication when it fails - the
-- objects just won't have been deleted.
--
-- Can remove graph table portion once graph tables are
-- removed.
-- ======================================================
DROP PROCEDURE IF EXISTS undelete_by_txn;
DELIMITER //
CREATE PROCEDURE undelete_by_txn(IN txnId BIGINT)

  BEGIN

  DECLARE EXIT HANDLER FOR SQLEXCEPTION, SQLWARNING
  BEGIN
    ROLLBACK;
  END;

  START TRANSACTION;

      -- un-delete nodes
      CALL undelete_node(txnId);

      -- un-delete flatedges
      -- must be called only after nodes undeleted
      CALL undelete_flatedge(txnId);

      -- un-delete remaining flat tables
      CALL undelete_flat_table(txnId, 'acknowledge');
      CALL undelete_flat_table(txnId, 'description');
      CALL undelete_flat_table(txnId, 'file');
      CALL undelete_flat_table(txnId, 'party');
      CALL undelete_flat_table(txnId, 'tag');
      CALL undelete_flat_table(txnId, 'work');

      -- un-delete from graph tables
      CALL undelete_graph_table(txnId, 'vertex');
      CALL undelete_graph_table(txnId, 'edge');
      CALL undelete_graph_table(txnId, 'property');

    COMMIT;

  END //
DELIMITER ;
