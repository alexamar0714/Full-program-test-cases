# Script to remove duplicate current property values created by a bug in amberdb (now corrected)
CREATE TABLE min_txn_starts AS
SELECT id, name, MIN(txn_start) min
FROM property
WHERE txn_end = 0
GROUP BY id, name
HAVING COUNT(id) > 1;

DELETE p FROM property p, min_txn_starts m 
WHERE p.id = m.id
AND p.name = m.name
AND p.txn_end = 0
AND p.txn_start > m.min;

DROP table min_txn_starts;
