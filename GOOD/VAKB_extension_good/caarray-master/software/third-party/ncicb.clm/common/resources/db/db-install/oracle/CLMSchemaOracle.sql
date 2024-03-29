-- ALTER TABLE LOG_MESSAGE
--  DROP PRIMARY KEY CASCADE
-- /

-- DROP TABLE LOG_MESSAGE CASCADE CONSTRAINTS
-- /

CREATE TABLE LOG_MESSAGE
(
  LOG_ID        NUMBER(19),
  APPLICATION   VARCHAR2(25 BYTE),
  SERVER        VARCHAR2(50 BYTE),
  CATEGORY      VARCHAR2(255 BYTE),
  THREAD        VARCHAR2(255 BYTE),
  USERNAME      VARCHAR2(255 BYTE),
  SESSION_ID    VARCHAR2(255 BYTE),
  MSG           CLOB,
  THROWABLE     CLOB,
  NDC           CLOB,
  CREATED_ON    NUMBER(19)                      NOT NULL,
  OBJECT_ID     VARCHAR2(255 BYTE),
  OBJECT_NAME   VARCHAR2(255 BYTE),
  ORGANIZATION  VARCHAR2(255 BYTE),
  OPERATION     VARCHAR2(50 BYTE)
)
/

CREATE SEQUENCE LOG_MESSAGE_ID_SEQ
increment by 1
start with 3
NOMAXVALUE
minvalue 1
nocycle
nocache
noorder
/


CREATE UNIQUE INDEX PRIMARY00000 ON LOG_MESSAGE
(LOG_ID)
/


CREATE INDEX APPLICATION_LOGTAB_INDX ON LOG_MESSAGE
(APPLICATION)
/


CREATE INDEX SERVER_LOGTAB_INDX ON LOG_MESSAGE
(SERVER)
/

CREATE INDEX THREAD_LOGTAB_INDX ON LOG_MESSAGE
(THREAD)
/


CREATE INDEX CREATED_ON_LOGTAB_INDX ON LOG_MESSAGE
(CREATED_ON)
/

CREATE OR REPLACE TRIGGER SET_LOG_MESSAGE_ID_SEQ
BEFORE INSERT
ON LOG_MESSAGE
FOR EACH ROW
BEGIN
  SELECT LOG_MESSAGE_ID_SEQ.NEXTVAL
  INTO :NEW.LOG_ID
  FROM DUAL;
END;
/
COMMIT
/

ALTER TABLE LOG_MESSAGE ADD (
  CONSTRAINT PRIMARY00000
 PRIMARY KEY
 (LOG_ID))
/



-- ALTER TABLE OBJECT_ATTRIBUTE
--  DROP PRIMARY KEY CASCADE
-- /

-- DROP TABLE OBJECT_ATTRIBUTE CASCADE CONSTRAINTS
-- /

CREATE TABLE OBJECT_ATTRIBUTE
(
  OBJECT_ATTRIBUTE_ID  NUMBER(19),
  CURRENT_VALUE        VARCHAR2(255 BYTE),
  PREVIOUS_VALUE       VARCHAR2(255 BYTE),
  ATTRIBUTE            VARCHAR2(255 BYTE)       NOT NULL
)
/

CREATE SEQUENCE OBJECT_ATTRIBUTE_ID_SEQ
increment by 1
start with 3
NOMAXVALUE
minvalue 1
nocycle
nocache
noorder
/

CREATE OR REPLACE TRIGGER SET_OBJECT_ATTRIBUTE_ID_SEQ
BEFORE INSERT
ON OBJECT_ATTRIBUTE
FOR EACH ROW
BEGIN
  SELECT OBJECT_ATTRIBUTE_ID_SEQ.NEXTVAL
  INTO :NEW.OBJECT_ATTRIBUTE_ID
  FROM DUAL;
END;
/
COMMIT
/

CREATE UNIQUE INDEX PRIMARY00001 ON OBJECT_ATTRIBUTE
(OBJECT_ATTRIBUTE_ID)
/


ALTER TABLE OBJECT_ATTRIBUTE ADD (
  CONSTRAINT PRIMARY00001
 PRIMARY KEY
 (OBJECT_ATTRIBUTE_ID))
/


-- DROP TABLE OBJECTATTRIBUTES CASCADE CONSTRAINTS
-- /

CREATE TABLE OBJECTATTRIBUTES
(
  LOG_ID               NUMBER(19)               NOT NULL,
  OBJECT_ATTRIBUTE_ID  NUMBER(19)               NOT NULL
)
/


CREATE INDEX "Index_2" ON OBJECTATTRIBUTES
(LOG_ID)
/

CREATE INDEX FK_OBJECTATTRIBUTES_2 ON OBJECTATTRIBUTES
(OBJECT_ATTRIBUTE_ID)
/

COMMIT
/