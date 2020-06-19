use amberdb;

DROP TABLE IF EXISTS node_history;
CREATE TABLE IF NOT EXISTS node_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        accessConditions                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                              expiryDate                                DATETIME,
                internalAccessConditions                                    TEXT,
                       localSystemNumber                             VARCHAR(63),
                                    name                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                            recordSource                             VARCHAR(63),
                         restrictionType                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

insert into node_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, convert(value using 'utf8mb4') FROM property where name='type';
update node_history t, property p set t.accessConditions = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessConditions';
update node_history t, property p set t.alias = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'alias';
update node_history t, property p set t.commentsExternal = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commentsExternal';
update node_history t, property p set t.commentsInternal = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commentsInternal';
update node_history t, property p set t.expiryDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'expiryDate';
update node_history t, property p set t.internalAccessConditions = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'internalAccessConditions';
update node_history t, property p set t.localSystemNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'localSystemNumber';
update node_history t, property p set t.name = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'name';
update node_history t, property p set t.notes = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'notes';
update node_history t, property p set t.recordSource = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'recordSource';
update node_history t, property p set t.restrictionType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'restrictionType';
CREATE INDEX node_history_type   ON node_history (type);
CREATE INDEX node_history_id     ON node_history (id);
CREATE INDEX node_history_txn_id ON node_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS node;
CREATE TABLE IF NOT EXISTS node (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        accessConditions                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                              expiryDate                                DATETIME,
                internalAccessConditions                                    TEXT,
                       localSystemNumber                             VARCHAR(63),
                                    name                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                            recordSource                             VARCHAR(63),
                         restrictionType                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
insert into node select * from node_history where txn_end = 0;
CREATE INDEX node_id ON node (id);
CREATE INDEX node_txn_id ON node (id, txn_start, txn_end);
CREATE INDEX node_type ON node (type);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_node;
CREATE TABLE IF NOT EXISTS sess_node (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        accessConditions                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                              expiryDate                                DATETIME,
                internalAccessConditions                                    TEXT,
                       localSystemNumber                             VARCHAR(63),
                                    name                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                            recordSource                             VARCHAR(63),
                         restrictionType                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_node_id ON sess_node (id);
CREATE INDEX sess_node_txn_id ON sess_node (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS work_history;
CREATE TABLE IF NOT EXISTS work_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                abstract                                    TEXT,
                                  access                                    TEXT,
                         accessAgreement                             VARCHAR(63),
          accessAgreementReasonForChange                             VARCHAR(63),
       accessAgreementReasonForChangeSum                                    TEXT,
                          accessComments                                    TEXT,
                        accessConditions                             VARCHAR(63),
          accessConditionReasonForChange                             VARCHAR(63),
       accessConditionReasonForChangeSum                                    TEXT,
                     acquisitionCategory                             VARCHAR(63),
                       acquisitionStatus                             VARCHAR(63),
                   additionalContributor                                    TEXT,
                       additionalCreator                            VARCHAR(255),
                        additionalSeries                            VARCHAR(255),
               additionalSeriesStatement                            VARCHAR(255),
                         additionalTitle                                    TEXT,
                               addressee                            VARCHAR(255),
                               adminInfo                            VARCHAR(255),
                             advertising                                 BOOLEAN,
                               algorithm                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                    allowHighResdownload                                 BOOLEAN,
                       allowOnsiteAccess                                 BOOLEAN,
                        alternativeTitle                             VARCHAR(63),
                                 altform                            VARCHAR(255),
                             arrangement                                    TEXT,
                       australianContent                                 BOOLEAN,
                                bestCopy                              VARCHAR(1),
                                   bibId                             VARCHAR(63),
                                bibLevel                             VARCHAR(63),
                            bibliography                                    TEXT,
                                captions                                    TEXT,
                                 carrier                             VARCHAR(63),
                                category                            VARCHAR(255),
                              childRange                            VARCHAR(255),
                          classification                            VARCHAR(255),
                              collection                             VARCHAR(63),
                        collectionNumber                             VARCHAR(63),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                        commercialStatus                             VARCHAR(63),
         commercialStatusReasonForChange                             VARCHAR(63),
      commercialStatusReasonForChangeSum                                    TEXT,
                           copyCondition                                    TEXT,
                  availabilityConstraint                                    TEXT,
                             contributor                                    TEXT,
                             coordinates                            VARCHAR(255),
                       copyingPublishing                                    TEXT,
                         copyrightPolicy                             VARCHAR(63),
                                copyRole                             VARCHAR(63),
                              copyStatus                             VARCHAR(63),
                                copyType                             VARCHAR(63),
                    correspondenceHeader                            VARCHAR(255),
                        correspondenceId                            VARCHAR(255),
                     correspondenceIndex                            VARCHAR(255),
                                coverage                            VARCHAR(255),
                                 creator                                    TEXT,
                        creatorStatement                                    TEXT,
                          currentVersion                             VARCHAR(63),
                             dateCreated                                DATETIME,
                           dateRangeInAS                             VARCHAR(63),
                                dcmAltPi                             VARCHAR(63),
                              dcmCopyPid                             VARCHAR(63),
                      dcmDateTimeCreated                                DATETIME,
                      dcmDateTimeUpdated                                DATETIME,
                        dcmRecordCreator                             VARCHAR(63),
                        dcmRecordUpdater                             VARCHAR(63),
                           dcmSourceCopy                            VARCHAR(255),
                              dcmWorkPid                             VARCHAR(63),
                             depositType                             VARCHAR(63),
                           digitalStatus                             VARCHAR(63),
                       digitalStatusDate                                DATETIME,
                        displayTitlePage                                 BOOLEAN,
                 eadUpdateReviewRequired                              VARCHAR(1),
                                 edition                            VARCHAR(255),
                           encodingLevel                             VARCHAR(63),
                                endChild                            VARCHAR(255),
                                 endDate                                DATETIME,
                               eventNote                            VARCHAR(255),
                              exhibition                            VARCHAR(255),
                              expiryDate                                DATETIME,
                                  extent                                    TEXT,
                          findingAidNote                                    TEXT,
                               firstPart                             VARCHAR(63),
                                  folder                                    TEXT,
                            folderNumber                             VARCHAR(63),
                              folderType                             VARCHAR(63),
                                    form                             VARCHAR(63),
                                   genre                             VARCHAR(63),
                                 heading                            VARCHAR(255),
                               holdingId                             VARCHAR(63),
                           holdingNumber                                    TEXT,
                                    html                                    TEXT,
                             illustrated                                 BOOLEAN,
                        ilmsSentDateTime                                DATETIME,
                               immutable                             VARCHAR(63),
                             ingestJobId                                  BIGINT,
               interactiveIndexAvailable                                 BOOLEAN,
                internalAccessConditions                                    TEXT,
                           isMissingPage                                 BOOLEAN,
                                    issn                            VARCHAR(255),
                               issueDate                                DATETIME,
                                language                             VARCHAR(63),
                       localSystemNumber                             VARCHAR(63),
                            manipulation                             VARCHAR(63),
             materialFromMultipleSources                                 BOOLEAN,
                            materialType                             VARCHAR(63),
                                  metsId                             VARCHAR(63),
                 moreIlmsDetailsRequired                                 BOOLEAN,
                                   notes                            VARCHAR(255),
                              occupation                            VARCHAR(255),
                            otherNumbers                             VARCHAR(63),
                              otherTitle                                    TEXT,
                        parentConstraint                             VARCHAR(63),
                       preferredCitation                                    TEXT,
                            preservicaId                             VARCHAR(63),
                          preservicaType                             VARCHAR(63),
                       printedPageNumber                             VARCHAR(63),
                              provenance                                    TEXT,
                     publicationCategory                             VARCHAR(63),
                        publicationLevel                            VARCHAR(255),
                             publicNotes                             VARCHAR(63),
                               publisher                                    TEXT,
              rdsAcknowledgementReceiver                            VARCHAR(255),
                  rdsAcknowledgementType                             VARCHAR(63),
                            recordSource                             VARCHAR(63),
                         relatedMaterial                                    TEXT,
                              repository                            VARCHAR(255),
                        representativeId                             VARCHAR(63),
                    restrictionsOnAccess                                    TEXT,
                         restrictionType                            VARCHAR(255),
                                  rights                                    TEXT,
                                scaleEtc                                    TEXT,
                            scopeContent                                    TEXT,
                        segmentIndicator                            VARCHAR(255),
                              sendToIlms                                 BOOLEAN,
		              sendToIlmsDateTime                             VARCHAR(63),
                       sensitiveMaterial                             VARCHAR(63),
                         sensitiveReason                             VARCHAR(63),
                                  series                                    TEXT,
                       sheetCreationDate                            VARCHAR(255),
                               sheetName                             VARCHAR(63),
                              standardId                                    TEXT,
                              startChild                             VARCHAR(63),
                               startDate                                DATETIME,
                             subHeadings                            VARCHAR(255),
                                 subject                                    TEXT,
                                 subType                             VARCHAR(63),
                               subUnitNo                                    TEXT,
                             subUnitType                             VARCHAR(63),
                                 summary                                    TEXT,
                             tempHolding                             VARCHAR(63),
                            tilePosition                                    TEXT,
                             timedStatus                             VARCHAR(63),
                                   title                                    TEXT,
                           totalDuration                             VARCHAR(63),
                            uniformTitle                            VARCHAR(255),
                                vendorId                             VARCHAR(63),
                           versionNumber                              VARCHAR(1),
              workCreatedDuringMigration                                 BOOLEAN,
                                 workPid                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                 
insert into work_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, type from node_history where type='Work' or type='EADWork' or type='Page' or type='Copy' or type='Section';
update work_history t, property p set t.type = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'type';
update work_history t, property p set t.abstract = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'abstract';
update work_history t, property p set t.access = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'access';
update work_history t, property p set t.accessAgreement = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessAgreement';
update work_history t, property p set t.accessAgreementReasonForChange = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessAgreementReasonForChange';
update work_history t, property p set t.accessAgreementReasonForChangeSum = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessAgreementReasonForChangeSum';
update work_history t, property p set t.accessComments = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessComments';
update work_history t, property p set t.accessConditions = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessConditions';
update work_history t, property p set t.accessConditionReasonForChange = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessConditionReasonForChange';
update work_history t, property p set t.accessConditionReasonForChangeSum = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'accessConditionReasonForChangeSum';
update work_history t, property p set t.acquisitionCategory = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'acquisitionCategory';
update work_history t, property p set t.acquisitionStatus = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'acquisitionStatus';
update work_history t, property p set t.additionalContributor = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'additionalContributor';
update work_history t, property p set t.additionalCreator = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'additionalCreator';
update work_history t, property p set t.additionalSeries = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'additionalSeries';
update work_history t, property p set t.additionalSeriesStatement = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'additionalSeriesStatement';
update work_history t, property p set t.additionalTitle = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'additionalTitle';
update work_history t, property p set t.addressee = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'addressee';
update work_history t, property p set t.adminInfo = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'adminInfo';
update work_history t, property p set t.advertising = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'advertising';
update work_history t, property p set t.algorithm = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'algorithm';
update work_history t, property p set t.alias = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'alias';
update work_history t, property p set t.allowHighResdownload = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'allowHighResdownload';
update work_history t, property p set t.allowOnsiteAccess = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'allowOnsiteAccess';
update work_history t, property p set t.alternativeTitle = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'alternativeTitle';
update work_history t, property p set t.altform = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'altform';
update work_history t, property p set t.arrangement = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'arrangement';
update work_history t, property p set t.australianContent = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'australianContent';
update work_history t, property p set t.bestCopy = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bestCopy';
update work_history t, property p set t.bibId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bibId';
update work_history t, property p set t.bibLevel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bibLevel';
update work_history t, property p set t.bibliography = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bibliography';
update work_history t, property p set t.carrier = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'carrier';
update work_history t, property p set t.category = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'category';
update work_history t, property p set t.childRange = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'childRange';
update work_history t, property p set t.classification = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'classification';
update work_history t, property p set t.collection = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'collection';
update work_history t, property p set t.collectionNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'collectionNumber';
update work_history t, property p set t.commentsExternal = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commentsExternal';
update work_history t, property p set t.commentsInternal = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commentsInternal';
update work_history t, property p set t.commercialStatus = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commercialStatus';
update work_history t, property p set t.commercialStatusReasonForChange = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commercialStatusReasonForChange';
update work_history t, property p set t.commercialStatusReasonForChangeSum = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'commercialStatusReasonForChangeSum';
update work_history t, property p set t.copyCondition = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'condition';
update work_history t, property p set t.availabilityConstraint = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'constraint';
update work_history t, property p set t.contributor = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'contributor';
update work_history t, property p set t.coordinates = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'coordinates';
update work_history t, property p set t.copyingPublishing = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'copyingPublishing';
update work_history t, property p set t.copyrightPolicy = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'copyrightPolicy';
update work_history t, property p set t.copyRole = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'copyRole';
update work_history t, property p set t.copyStatus = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'copyStatus';
update work_history t, property p set t.copyType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'copyType';
update work_history t, property p set t.correspondenceHeader = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'correspondenceHeader';
update work_history t, property p set t.correspondenceId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'correspondenceId';
update work_history t, property p set t.correspondenceIndex = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'correspondenceIndex';
update work_history t, property p set t.coverage = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'coverage';
update work_history t, property p set t.creator = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'creator';
update work_history t, property p set t.creatorStatement = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'creatorStatement';
update work_history t, property p set t.currentVersion = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'currentVersion';
update work_history t, property p set t.dateCreated = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateCreated';
update work_history t, property p set t.dateRangeInAS = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateRangeInAS';
update work_history t, property p set t.dcmAltPi = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmAltPi';
update work_history t, property p set t.dcmCopyPid = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmCopyPid';
update work_history t, property p set t.dcmDateTimeCreated = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmDateTimeCreated';
update work_history t, property p set t.dcmDateTimeUpdated = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmDateTimeUpdated';
update work_history t, property p set t.dcmRecordCreator = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmRecordCreator';
update work_history t, property p set t.dcmRecordUpdater = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmRecordUpdater';
update work_history t, property p set t.dcmSourceCopy = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmSourceCopy';
update work_history t, property p set t.dcmWorkPid = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmWorkPid';
update work_history t, property p set t.depositType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'depositType';
update work_history t, property p set t.digitalStatus = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'digitalStatus';
update work_history t, property p set t.digitalStatusDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'digitalStatusDate';
update work_history t, property p set t.displayTitlePage = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'displayTitlePage';
update work_history t, property p set t.eadUpdateReviewRequired = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'eadUpdateReviewRequired';
update work_history t, property p set t.edition = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'edition';
update work_history t, property p set t.encodingLevel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'encodingLevel';
update work_history t, property p set t.endChild = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'endChild';
update work_history t, property p set t.endDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'endDate';
update work_history t, property p set t.eventNote = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'eventNote';
update work_history t, property p set t.exhibition = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'exhibition';
update work_history t, property p set t.expiryDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'expiryDate';
update work_history t, property p set t.extent = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'extent';
update work_history t, property p set t.findingAidNote = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'findingAidNote';
update work_history t, property p set t.firstPart = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'firstPart';
update work_history t, property p set t.folder = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'folder';
update work_history t, property p set t.folderNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'folderNumber';
update work_history t, property p set t.folderType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'folderType';
update work_history t, property p set t.form = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'form';
update work_history t, property p set t.genre = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'genre';
update work_history t, property p set t.heading = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'heading';
update work_history t, property p set t.holdingId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'holdingId';
update work_history t, property p set t.holdingNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'holdingNumber';
update work_history t, property p set t.html = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'html';
update work_history t, property p set t.illustrated = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'illustrated';
update work_history t, property p set t.ilmsSentDateTime = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'ilmsSentDateTime';
update work_history t, property p set t.immutable = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'immutable';
update work_history t, property p set t.ingestJobId = conv(hex(value),16,10)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'ingestJobId';
update work_history t, property p set t.interactiveIndexAvailable = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'interactiveIndexAvailable';
update work_history t, property p set t.internalAccessConditions = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'internalAccessConditions';
update work_history t, property p set t.isMissingPage = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'isMissingPage';
update work_history t, property p set t.issn = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'issn';
update work_history t, property p set t.issueDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'issueDate';
update work_history t, property p set t.language = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'language';
update work_history t, property p set t.localSystemNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'localSystemNumber';
update work_history t, property p set t.manipulation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'manipulation';
update work_history t, property p set t.materialFromMultipleSources = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'materialFromMultipleSources';
update work_history t, property p set t.materialType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'materialType';
update work_history t, property p set t.metsId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'metsId';
update work_history t, property p set t.moreIlmsDetailsRequired = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'moreIlmsDetailsRequired';
update work_history t, property p set t.notes = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'notes';
update work_history t, property p set t.occupation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'occupation';
update work_history t, property p set t.otherNumbers = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'otherNumbers';
update work_history t, property p set t.otherTitle = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'otherTitle';
update work_history t, property p set t.parentConstraint = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'parentConstraint';
update work_history t, property p set t.preferredCitation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'preferredCitation';
update work_history t, property p set t.preservicaId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'preservicaId';
update work_history t, property p set t.preservicaType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'preservicaType';
update work_history t, property p set t.printedPageNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'printedPageNumber';
update work_history t, property p set t.provenance = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'provenance';
update work_history t, property p set t.publicationCategory = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'publicationCategory';
update work_history t, property p set t.publicationLevel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'publicationLevel';
update work_history t, property p set t.publicNotes = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'publicNotes';
update work_history t, property p set t.publisher = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'publisher';
update work_history t, property p set t.rdsAcknowledgementReceiver = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'rdsAcknowledgementReceiver';
update work_history t, property p set t.rdsAcknowledgementType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'rdsAcknowledgementType';
update work_history t, property p set t.recordSource = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'recordSource';
update work_history t, property p set t.relatedMaterial = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'relatedMaterial';
update work_history t, property p set t.repository = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'repository';
update work_history t, property p set t.representativeId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'representativeId';
update work_history t, property p set t.restrictionsOnAccess = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'restrictionsOnAccess';
update work_history t, property p set t.restrictionType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'restrictionType';
update work_history t, property p set t.rights = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'rights';
update work_history t, property p set t.scaleEtc = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'scaleEtc';
update work_history t, property p set t.scopeContent = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'scopeContent';
update work_history t, property p set t.segmentIndicator = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'segmentIndicator';
update work_history t, property p set t.sendToIlms = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sendToIlms';
update work_history t, property p set t.sendToIlmsDateTime = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sendToIlmsDateTime';
update work_history t, property p set t.sensitiveMaterial = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sensitiveMaterial';
update work_history t, property p set t.sensitiveReason = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sensitiveReason';
update work_history t, property p set t.series = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'series';
update work_history t, property p set t.sheetCreationDate = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sheetCreationDate';
update work_history t, property p set t.sheetName = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'sheetName';
update work_history t, property p set t.standardId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'standardId';
update work_history t, property p set t.startChild = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'startChild';
update work_history t, property p set t.startDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'startDate';
update work_history t, property p set t.subHeadings = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subHeadings';
update work_history t, property p set t.subject = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subject';
update work_history t, property p set t.subType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subType';
update work_history t, property p set t.subUnitNo = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subUnitNo';
update work_history t, property p set t.subUnitType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subUnitType';
update work_history t, property p set t.summary = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'summary';
update work_history t, property p set t.tempHolding = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'tempHolding';
update work_history t, property p set t.tilePosition = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'tilePosition';
update work_history t, property p set t.timedStatus = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'timedStatus';
update work_history t, property p set t.title = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'title';
update work_history t, property p set t.totalDuration = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'totalDuration';
update work_history t, property p set t.uniformTitle = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'uniformTitle';
update work_history t, property p set t.vendorId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'vendorId';
update work_history t, property p set t.versionNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'versionNumber';
update work_history t, property p set t.workCreatedDuringMigration = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'workCreatedDuringMigration';
update work_history t, property p set t.workPid = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'workPid';
                                 
CREATE INDEX work_history_id ON work_history (id);
CREATE INDEX work_history_txn_id ON work_history (id, txn_start, txn_end);
CREATE INDEX work_history_restricted_child ON work_history (accessConditions, bibLevel);
CREATE INDEX work_history_digitalStatus ON work_history (digitalStatus);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------


DROP TABLE IF EXISTS work;
CREATE TABLE IF NOT EXISTS work (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                abstract                                    TEXT,
                                  access                                    TEXT,
                         accessAgreement                             VARCHAR(63),
          accessAgreementReasonForChange                             VARCHAR(63),
       accessAgreementReasonForChangeSum                             VARCHAR(255),
                          accessComments                                    TEXT,
                        accessConditions                             VARCHAR(63),
          accessConditionReasonForChange                             VARCHAR(63),
       accessConditionReasonForChangeSum                                    TEXT,
                     acquisitionCategory                             VARCHAR(63),
                       acquisitionStatus                             VARCHAR(63),
                   additionalContributor                                    TEXT,
                       additionalCreator                            VARCHAR(255),
                        additionalSeries                            VARCHAR(255),
               additionalSeriesStatement                            VARCHAR(255),
                         additionalTitle                                    TEXT,
                               addressee                            VARCHAR(255),
                               adminInfo                            VARCHAR(255),
                             advertising                                 BOOLEAN,
                               algorithm                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                    allowHighResdownload                                 BOOLEAN,
                       allowOnsiteAccess                                 BOOLEAN,
                        alternativeTitle                             VARCHAR(63),
                                 altform                            VARCHAR(255),
                             arrangement                                    TEXT,
                       australianContent                                 BOOLEAN,
                                bestCopy                              VARCHAR(1),
                                   bibId                             VARCHAR(63),
                                bibLevel                             VARCHAR(63),
                            bibliography                                    TEXT,
                                captions                                    TEXT,
                                 carrier                             VARCHAR(63),
                                category                            VARCHAR(255),
                              childRange                            VARCHAR(255),
                          classification                            VARCHAR(255),
                              collection                             VARCHAR(63),
                        collectionNumber                             VARCHAR(63),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                        commercialStatus                             VARCHAR(63),
         commercialStatusReasonForChange                             VARCHAR(63),
      commercialStatusReasonForChangeSum                                    TEXT,
                           copyCondition                                    TEXT,
                  availabilityConstraint                                    TEXT,
                             contributor                                    TEXT,
                             coordinates                            VARCHAR(255),
                       copyingPublishing                                    TEXT,
                         copyrightPolicy                             VARCHAR(63),
                                copyRole                             VARCHAR(63),
                              copyStatus                             VARCHAR(63),
                                copyType                             VARCHAR(63),
                    correspondenceHeader                            VARCHAR(255),
                        correspondenceId                            VARCHAR(255),
                     correspondenceIndex                            VARCHAR(255),
                                coverage                            VARCHAR(255),
                                 creator                                    TEXT,
                        creatorStatement                                    TEXT,
                          currentVersion                             VARCHAR(63),
                             dateCreated                                DATETIME,
                           dateRangeInAS                             VARCHAR(63),
                                dcmAltPi                             VARCHAR(63),
                              dcmCopyPid                             VARCHAR(63),
                      dcmDateTimeCreated                                DATETIME,
                      dcmDateTimeUpdated                                DATETIME,
                        dcmRecordCreator                             VARCHAR(63),
                        dcmRecordUpdater                             VARCHAR(63),
                           dcmSourceCopy                            VARCHAR(255),
                              dcmWorkPid                             VARCHAR(63),
                             depositType                             VARCHAR(63),
                           digitalStatus                             VARCHAR(63),
                       digitalStatusDate                                DATETIME,
                        displayTitlePage                                 BOOLEAN,
                 eadUpdateReviewRequired                              VARCHAR(1),
                                 edition                            VARCHAR(255),
                           encodingLevel                             VARCHAR(63),
                                endChild                            VARCHAR(255),
                                 endDate                                DATETIME,
                               eventNote                            VARCHAR(255),
                              exhibition                            VARCHAR(255),
                              expiryDate                                DATETIME,
                                  extent                                    TEXT,
                          findingAidNote                                    TEXT,
                               firstPart                             VARCHAR(63),
                                  folder                                    TEXT,
                            folderNumber                             VARCHAR(63),
                              folderType                             VARCHAR(63),
                                    form                             VARCHAR(63),
                                   genre                             VARCHAR(63),
                                 heading                            VARCHAR(255),
                               holdingId                             VARCHAR(63),
                           holdingNumber                                    TEXT,
                                    html                                    TEXT,
                             illustrated                                 BOOLEAN,
                        ilmsSentDateTime                                DATETIME,
                               immutable                             VARCHAR(63),
                             ingestJobId                                  BIGINT,
               interactiveIndexAvailable                                 BOOLEAN,
                internalAccessConditions                                    TEXT,
                           isMissingPage                                 BOOLEAN,
                                    issn                            VARCHAR(255),
                               issueDate                                DATETIME,
                                language                             VARCHAR(63),
                       localSystemNumber                             VARCHAR(63),
                            manipulation                             VARCHAR(63),
             materialFromMultipleSources                                 BOOLEAN,
                            materialType                             VARCHAR(63),
                                  metsId                             VARCHAR(63),
                 moreIlmsDetailsRequired                                 BOOLEAN,
                                   notes                            VARCHAR(255),
                              occupation                            VARCHAR(255),
                            otherNumbers                             VARCHAR(63),
                              otherTitle                                    TEXT,
                        parentConstraint                             VARCHAR(63),
                       preferredCitation                                    TEXT,
                            preservicaId                             VARCHAR(63),
                          preservicaType                             VARCHAR(63),
                       printedPageNumber                             VARCHAR(63),
                              provenance                                    TEXT,
                     publicationCategory                             VARCHAR(63),
                        publicationLevel                            VARCHAR(255),
                             publicNotes                             VARCHAR(63),
                               publisher                                    TEXT,
              rdsAcknowledgementReceiver                            VARCHAR(255),
                  rdsAcknowledgementType                             VARCHAR(63),
                            recordSource                             VARCHAR(63),
                         relatedMaterial                                    TEXT,
                              repository                            VARCHAR(255),
                        representativeId                             VARCHAR(63),
                    restrictionsOnAccess                                    TEXT,
                         restrictionType                            VARCHAR(255),
                                  rights                                    TEXT,
                                scaleEtc                                    TEXT,
                            scopeContent                                    TEXT,
                        segmentIndicator                            VARCHAR(255),
                              sendToIlms                                 BOOLEAN,
					  sendToIlmsDateTime                             VARCHAR(63),
                       sensitiveMaterial                             VARCHAR(63),
                         sensitiveReason                             VARCHAR(63),
                                  series                                    TEXT,
                       sheetCreationDate                            VARCHAR(255),
                               sheetName                             VARCHAR(63),
                              standardId                                    TEXT,
                              startChild                             VARCHAR(63),
                               startDate                                DATETIME,
                             subHeadings                            VARCHAR(255),
                                 subject                                    TEXT,
                                 subType                             VARCHAR(63),
                               subUnitNo                                    TEXT,
                             subUnitType                             VARCHAR(63),
                                 summary                                    TEXT,
                             tempHolding                             VARCHAR(63),
                            tilePosition                                    TEXT,
                             timedStatus                             VARCHAR(63),
                                   title                                    TEXT,
                           totalDuration                             VARCHAR(63),
                            uniformTitle                            VARCHAR(255),
                                vendorId                             VARCHAR(63),
                           versionNumber                              VARCHAR(1),
              workCreatedDuringMigration                                 BOOLEAN,
                                 workPid                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                 
insert into work select * from work_history where txn_end = 0;
                                 
CREATE INDEX work_id ON work (id);
CREATE INDEX work_txn_id ON work (id, txn_start, txn_end);
CREATE INDEX work_restricted_child ON work (accessConditions, bibLevel);
CREATE INDEX work_digitalStatus ON work (digitalStatus);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_work;
CREATE TABLE IF NOT EXISTS sess_work (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                abstract                                    TEXT,
                                  access                                    TEXT,
                         accessAgreement                             VARCHAR(63),
          accessAgreementReasonForChange                             VARCHAR(63),
       accessAgreementReasonForChangeSum                                    TEXT,
                          accessComments                                    TEXT,
                        accessConditions                             VARCHAR(63),
          accessConditionReasonForChange                             VARCHAR(63),
       accessConditionReasonForChangeSum                                    TEXT,
                     acquisitionCategory                             VARCHAR(63),
                       acquisitionStatus                             VARCHAR(63),
                   additionalContributor                                    TEXT,
                       additionalCreator                            VARCHAR(255),
                        additionalSeries                            VARCHAR(255),
               additionalSeriesStatement                            VARCHAR(255),
                         additionalTitle                                    TEXT,
                               addressee                            VARCHAR(255),
                               adminInfo                            VARCHAR(255),
                             advertising                                 BOOLEAN,
                               algorithm                             VARCHAR(63),
                                   alias                            VARCHAR(255),
                    allowHighResdownload                                 BOOLEAN,
                       allowOnsiteAccess                                 BOOLEAN,
                        alternativeTitle                             VARCHAR(63),
                                 altform                            VARCHAR(255),
                             arrangement                                    TEXT,
                       australianContent                                 BOOLEAN,
                                bestCopy                              VARCHAR(1),
                                   bibId                             VARCHAR(63),
                                bibLevel                             VARCHAR(63),
                            bibliography                                    TEXT,
                                captions                                    TEXT,
                                 carrier                             VARCHAR(63),
                                category                            VARCHAR(255),
                              childRange                            VARCHAR(255),
                          classification                            VARCHAR(255),
                              collection                             VARCHAR(63),
                        collectionNumber                             VARCHAR(63),
                        commentsExternal                                    TEXT,
                        commentsInternal                                    TEXT,
                        commercialStatus                             VARCHAR(63),
         commercialStatusReasonForChange                             VARCHAR(63),
      commercialStatusReasonForChangeSum                                    TEXT,
                           copyCondition                                    TEXT,
                  availabilityConstraint                                    TEXT,
                             contributor                                    TEXT,
                             coordinates                            VARCHAR(255),
                       copyingPublishing                                    TEXT,
                         copyrightPolicy                             VARCHAR(63),
                                copyRole                             VARCHAR(63),
                              copyStatus                             VARCHAR(63),
                                copyType                             VARCHAR(63),
                    correspondenceHeader                            VARCHAR(255),
                        correspondenceId                            VARCHAR(255),
                     correspondenceIndex                            VARCHAR(255),
                                coverage                            VARCHAR(255),
                                 creator                                    TEXT,
                        creatorStatement                                    TEXT,
                          currentVersion                             VARCHAR(63),
                             dateCreated                                DATETIME,
                           dateRangeInAS                             VARCHAR(63),
                                dcmAltPi                             VARCHAR(63),
                              dcmCopyPid                             VARCHAR(63),
                      dcmDateTimeCreated                                DATETIME,
                      dcmDateTimeUpdated                                DATETIME,
                        dcmRecordCreator                             VARCHAR(63),
                        dcmRecordUpdater                             VARCHAR(63),
                           dcmSourceCopy                            VARCHAR(255),
                              dcmWorkPid                             VARCHAR(63),
                             depositType                             VARCHAR(63),
                           digitalStatus                             VARCHAR(63),
                       digitalStatusDate                                DATETIME,
                        displayTitlePage                                 BOOLEAN,
                 eadUpdateReviewRequired                              VARCHAR(1),
                                 edition                            VARCHAR(255),
                           encodingLevel                             VARCHAR(63),
                                endChild                            VARCHAR(255),
                                 endDate                                DATETIME,
                               eventNote                            VARCHAR(255),
                              exhibition                            VARCHAR(255),
                              expiryDate                                DATETIME,
                                  extent                                    TEXT,
                          findingAidNote                                    TEXT,
                               firstPart                             VARCHAR(63),
                                  folder                                    TEXT,
                            folderNumber                             VARCHAR(63),
                              folderType                             VARCHAR(63),
                                    form                             VARCHAR(63),
                                   genre                             VARCHAR(63),
                                 heading                            VARCHAR(255),
                               holdingId                             VARCHAR(63),
                           holdingNumber                                    TEXT,
                                    html                                    TEXT,
                             illustrated                                 BOOLEAN,
                        ilmsSentDateTime                                DATETIME,
                               immutable                             VARCHAR(63),
                             ingestJobId                                  BIGINT,
               interactiveIndexAvailable                                 BOOLEAN,
                internalAccessConditions                                    TEXT,
                           isMissingPage                                 BOOLEAN,
                                    issn                            VARCHAR(255),
                               issueDate                                DATETIME,
                                language                             VARCHAR(63),
                       localSystemNumber                             VARCHAR(63),
                            manipulation                             VARCHAR(63),
             materialFromMultipleSources                                 BOOLEAN,
                            materialType                             VARCHAR(63),
                                  metsId                             VARCHAR(63),
                 moreIlmsDetailsRequired                                 BOOLEAN,
                                   notes                            VARCHAR(255),
                              occupation                            VARCHAR(255),
                            otherNumbers                             VARCHAR(63),
                              otherTitle                                    TEXT,
                        parentConstraint                             VARCHAR(63),
                       preferredCitation                                    TEXT,
                            preservicaId                             VARCHAR(63),
                          preservicaType                             VARCHAR(63),
                       printedPageNumber                             VARCHAR(63),
                              provenance                                    TEXT,
                     publicationCategory                             VARCHAR(63),
                        publicationLevel                            VARCHAR(255),
                             publicNotes                             VARCHAR(63),
                               publisher                                    TEXT,
              rdsAcknowledgementReceiver                            VARCHAR(255),
                  rdsAcknowledgementType                             VARCHAR(63),
                            recordSource                             VARCHAR(63),
                         relatedMaterial                                    TEXT,
                              repository                            VARCHAR(255),
                        representativeId                             VARCHAR(63),
                    restrictionsOnAccess                                    TEXT,
                         restrictionType                            VARCHAR(255),
                                  rights                                    TEXT,
                                scaleEtc                                    TEXT,
                            scopeContent                                    TEXT,
                        segmentIndicator                            VARCHAR(255),
                              sendToIlms                                 BOOLEAN,
					  sendToIlmsDateTime                             VARCHAR(63),
                       sensitiveMaterial                             VARCHAR(63),
                         sensitiveReason                             VARCHAR(63),
                                  series                                    TEXT,
                       sheetCreationDate                            VARCHAR(255),
                               sheetName                             VARCHAR(63),
                              standardId                                    TEXT,
                              startChild                             VARCHAR(63),
                               startDate                                DATETIME,
                             subHeadings                            VARCHAR(255),
                                 subject                                    TEXT,
                                 subType                             VARCHAR(63),
                               subUnitNo                                    TEXT,
                             subUnitType                             VARCHAR(63),
                                 summary                                    TEXT,
                             tempHolding                             VARCHAR(63),
                            tilePosition                                    TEXT,
                             timedStatus                             VARCHAR(63),
                                   title                                    TEXT,
                           totalDuration                             VARCHAR(63),
                            uniformTitle                            VARCHAR(255),
                                vendorId                             VARCHAR(63),
                           versionNumber                              VARCHAR(1),
              workCreatedDuringMigration                                 BOOLEAN,
                                 workPid                                    TEXT) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_work_id ON sess_work (id);
CREATE INDEX sess_work_txn_id ON sess_work (id, txn_start, txn_end);
CREATE INDEX sess_work_restricted_child ON sess_work (accessConditions, bibLevel);
CREATE INDEX sess_work_digitalStatus ON sess_work (digitalStatus);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS file_history;
CREATE TABLE IF NOT EXISTS file_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                             application                                    TEXT,
                  applicationDateCreated                                DATETIME,
                                bitDepth                             VARCHAR(63),
                                 bitrate                             VARCHAR(63),
                                  blobId                                  BIGINT,
                              blockAlign                                 INTEGER,
                                   brand                             VARCHAR(63),
                         carrierCapacity                             VARCHAR(63),
                                 channel                             VARCHAR(63),
                                checksum                             VARCHAR(63),
                  checksumGenerationDate                                DATETIME,
                            checksumType                             VARCHAR(63),
                                   codec                             VARCHAR(63),
                           colourProfile                             VARCHAR(63),
                             colourSpace                             VARCHAR(63),
                             compression                             VARCHAR(63),
                              cpLocation                            VARCHAR(255),
                           dateDigitised                                DATETIME,
                              dcmCopyPid                             VARCHAR(63),
                                  device                             VARCHAR(63),
                      deviceSerialNumber                             VARCHAR(63),
                                duration                             VARCHAR(63),
                            durationType                             VARCHAR(63),
                                encoding                             VARCHAR(63),
                            equalisation                             VARCHAR(63),
                           fileContainer                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                       fileFormatVersion                             VARCHAR(63),
                                fileName                            VARCHAR(255),
                                fileSize                                  BIGINT,
                               framerate                                 INTEGER,
                             imageLength                                 INTEGER,
                              imageWidth                                 INTEGER,
                                location                            VARCHAR(255),
                        manufacturerMake                             VARCHAR(63),
                   manufacturerModelName                             VARCHAR(63),
                manufacturerSerialNumber                             VARCHAR(63),
                                mimeType                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                             orientation                             VARCHAR(63),
                             photometric                            VARCHAR(255),
                                reelSize                             VARCHAR(63),
                              resolution                             VARCHAR(63),
                          resolutionUnit                             VARCHAR(63),
                         samplesPerPixel                            VARCHAR(255),
                            samplingRate                             VARCHAR(63),
                                software                             VARCHAR(63),
                    softwareSerialNumber                             VARCHAR(63),
                              soundField                             VARCHAR(63),
                                   speed                             VARCHAR(63),
                                 surface                             VARCHAR(63),
                               thickness                             VARCHAR(63),
                                  toolId                             VARCHAR(63),
                               zoomLevel                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                               
insert into file_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, type from node_history where type='File' or type='ImageFile' or type='SoundFile' or type='MovingImageFile';
update file_history t, property p set t.application = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'application';
update file_history t, property p set t.applicationDateCreated = from_unixtime(conv(hex(value),16,10) / 1000) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'applicationDateCreated' and p.type = 'DTE';
update file_history t, property p set t.applicationDateCreated = str_to_date(value,'%Y:%m:%d %H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'applicationDateCreated' and p.type = 'STR' and p.value like '%:%:% %:%:%';
update file_history t, property p set t.applicationDateCreated = str_to_date(value,'%Y/%m/%d %H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'applicationDateCreated' and p.type = 'STR' and p.value like '%/%/% %:%:%';
update file_history t, property p set t.applicationDateCreated = str_to_date(value,'%Y-%m-%dT%H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'applicationDateCreated' and p.type = 'STR' and p.value like '%-%-%T%:%:%';
update file_history t, property p set t.bitDepth = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bitDepth';
update file_history t, property p set t.bitrate = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'bitrate';
update file_history t, property p set t.blobId = conv(hex(value),16,10)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'blobId';
update file_history t, property p set t.blockAlign = conv(hex(value),16,10) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'blockAlign';
update file_history t, property p set t.brand = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'brand';
update file_history t, property p set t.carrierCapacity = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'carrierCapacity';
update file_history t, property p set t.channel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'channel';
update file_history t, property p set t.checksum = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'checksum';
update file_history t, property p set t.checksumGenerationDate = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'checksumGenerationDate';
update file_history t, property p set t.checksumType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'checksumType';
update file_history t, property p set t.codec = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'codec';
update file_history t, property p set t.colourProfile = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'colourProfile';
update file_history t, property p set t.colourSpace = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'colourSpace';
update file_history t, property p set t.compression = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'compression';
update file_history t, property p set t.cpLocation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'cpLocation';
update file_history t, property p set t.dateDigitised = from_unixtime(conv(hex(value),16,10) / 1000) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateDigitised' and p.type = 'DTE';
update file_history t, property p set t.dateDigitised = str_to_date(value,'%Y:%m:%d %H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateDigitised' and p.type = 'STR' and p.value like '%:%:% %:%:%';
update file_history t, property p set t.dateDigitised = str_to_date(value,'%Y/%m/%d %H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateDigitised' and p.type = 'STR' and p.value like '%/%/% %:%:%';
update file_history t, property p set t.dateDigitised = str_to_date(value,'%Y-%m-%dT%H:%i:%s')       where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dateDigitised' and p.type = 'STR' and p.value like '%-%-%T%:%:%';
update file_history t, property p set t.dcmCopyPid = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'dcmCopyPid';
update file_history t, property p set t.device = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'device';
update file_history t, property p set t.deviceSerialNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'deviceSerialNumber';
update file_history t, property p set t.duration = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'duration';
update file_history t, property p set t.durationType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'durationType';
update file_history t, property p set t.encoding = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'encoding';
update file_history t, property p set t.equalisation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'equalisation';
update file_history t, property p set t.fileContainer = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileContainer';
update file_history t, property p set t.fileFormat = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileFormat';
update file_history t, property p set t.fileFormatVersion = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileFormatVersion';
update file_history t, property p set t.fileName = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileName';
update file_history t, property p set t.fileSize = conv(hex(value),16,10)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileSize';
update file_history t, property p set t.framerate = conv(hex(value),16,10) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'framerate';
update file_history t, property p set t.imageLength = conv(hex(value),16,10) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'imageLength';
update file_history t, property p set t.imageWidth = conv(hex(value),16,10) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'imageWidth';
update file_history t, property p set t.location = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'location';
update file_history t, property p set t.manufacturerMake = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'manufacturerMake';
update file_history t, property p set t.manufacturerModelName = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'manufacturerModelName';
update file_history t, property p set t.manufacturerSerialNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'manufacturerSerialNumber';
update file_history t, property p set t.mimeType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'mimeType';
update file_history t, property p set t.notes = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'notes';
update file_history t, property p set t.orientation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'orientation';
update file_history t, property p set t.photometric = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'photometric';
update file_history t, property p set t.reelSize = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'reelSize';
update file_history t, property p set t.resolution = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'resolution';
update file_history t, property p set t.resolutionUnit = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'resolutionUnit';
update file_history t, property p set t.samplesPerPixel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'samplesPerPixel';
update file_history t, property p set t.samplingRate = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'samplingRate';
update file_history t, property p set t.software = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'software';
update file_history t, property p set t.softwareSerialNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'softwareSerialNumber';
update file_history t, property p set t.soundField = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'soundField';
update file_history t, property p set t.speed = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'speed';
update file_history t, property p set t.surface = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'surface';
update file_history t, property p set t.thickness = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'thickness';
update file_history t, property p set t.toolId = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'toolId';
update file_history t, property p set t.zoomLevel = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'zoomLevel';

CREATE INDEX file_history_id ON file_history (id);
CREATE INDEX file_history_txn_id ON file_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS file;
CREATE TABLE IF NOT EXISTS file (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                             application                                    TEXT,
                  applicationDateCreated                                DATETIME,
                                bitDepth                             VARCHAR(63),
                                 bitrate                             VARCHAR(63),
                                  blobId                                  BIGINT,
                              blockAlign                                 INTEGER,
                                   brand                             VARCHAR(63),
                         carrierCapacity                             VARCHAR(63),
                                 channel                             VARCHAR(63),
                                checksum                             VARCHAR(63),
                  checksumGenerationDate                                DATETIME,
                            checksumType                             VARCHAR(63),
                                   codec                             VARCHAR(63),
                           colourProfile                             VARCHAR(63),
                             colourSpace                             VARCHAR(63),
                             compression                             VARCHAR(63),
                              cpLocation                            VARCHAR(255),
                           dateDigitised                                DATETIME,
                              dcmCopyPid                             VARCHAR(63),
                                  device                             VARCHAR(63),
                      deviceSerialNumber                             VARCHAR(63),
                                duration                             VARCHAR(63),
                            durationType                             VARCHAR(63),
                                encoding                             VARCHAR(63),
                            equalisation                             VARCHAR(63),
                           fileContainer                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                       fileFormatVersion                             VARCHAR(63),
                                fileName                            VARCHAR(255),
                                fileSize                                  BIGINT,
                               framerate                                 INTEGER,
                             imageLength                                 INTEGER,
                              imageWidth                                 INTEGER,
                                location                            VARCHAR(255),
                        manufacturerMake                             VARCHAR(63),
                   manufacturerModelName                             VARCHAR(63),
                manufacturerSerialNumber                             VARCHAR(63),
                                mimeType                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                             orientation                             VARCHAR(63),
                             photometric                            VARCHAR(255),
                                reelSize                             VARCHAR(63),
                              resolution                             VARCHAR(63),
                          resolutionUnit                             VARCHAR(63),
                         samplesPerPixel                            VARCHAR(255),
                            samplingRate                             VARCHAR(63),
                                software                             VARCHAR(63),
                    softwareSerialNumber                             VARCHAR(63),
                              soundField                             VARCHAR(63),
                                   speed                             VARCHAR(63),
                                 surface                             VARCHAR(63),
                               thickness                             VARCHAR(63),
                                  toolId                             VARCHAR(63),
                               zoomLevel                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                               
insert into file select * from file_history where txn_end = 0;

CREATE INDEX file_id ON file (id);
CREATE INDEX file_txn_id ON file (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_file;
CREATE TABLE IF NOT EXISTS sess_file (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                            application                                    TEXT,
                  applicationDateCreated                                DATETIME,
                                bitDepth                             VARCHAR(63),
                                 bitrate                             VARCHAR(63),
                                  blobId                                  BIGINT,
                              blockAlign                                 INTEGER,
                                   brand                             VARCHAR(63),
                         carrierCapacity                             VARCHAR(63),
                                 channel                             VARCHAR(63),
                                checksum                             VARCHAR(63),
                  checksumGenerationDate                                DATETIME,
                            checksumType                             VARCHAR(63),
                                   codec                             VARCHAR(63),
                           colourProfile                             VARCHAR(63),
                             colourSpace                             VARCHAR(63),
                             compression                             VARCHAR(63),
                              cpLocation                            VARCHAR(255),
                           dateDigitised                                DATETIME,
                              dcmCopyPid                             VARCHAR(63),
                                  device                             VARCHAR(63),
                      deviceSerialNumber                             VARCHAR(63),
                                duration                             VARCHAR(63),
                            durationType                             VARCHAR(63),
                                encoding                             VARCHAR(63),
                            equalisation                             VARCHAR(63),
                           fileContainer                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                       fileFormatVersion                             VARCHAR(63),
                                fileName                            VARCHAR(255),
                                fileSize                                  BIGINT,
                               framerate                                 INTEGER,
                             imageLength                                 INTEGER,
                              imageWidth                                 INTEGER,
                                location                            VARCHAR(255),
                        manufacturerMake                             VARCHAR(63),
                   manufacturerModelName                             VARCHAR(63),
                manufacturerSerialNumber                             VARCHAR(63),
                                mimeType                            VARCHAR(255),
                                   notes                            VARCHAR(255),
                             orientation                             VARCHAR(63),
                             photometric                            VARCHAR(255),
                                reelSize                             VARCHAR(63),
                              resolution                             VARCHAR(63),
                          resolutionUnit                             VARCHAR(63),
                         samplesPerPixel                            VARCHAR(255),
                            samplingRate                             VARCHAR(63),
                                software                             VARCHAR(63),
                    softwareSerialNumber                             VARCHAR(63),
                              soundField                             VARCHAR(63),
                                   speed                             VARCHAR(63),
                                 surface                             VARCHAR(63),
                               thickness                             VARCHAR(63),
                                  toolId                             VARCHAR(63),
                               zoomLevel                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_file_id ON sess_file (id);
CREATE INDEX sess_file_txn_id ON sess_file (id, txn_start, txn_end);


-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS description_history;
CREATE TABLE IF NOT EXISTS description_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        alternativeTitle                             VARCHAR(63),
                                    city                             VARCHAR(63),
                                 country                            VARCHAR(255),
                       digitalSourceType                            VARCHAR(255),
                                   event                            VARCHAR(255),
                         exposureFNumber                             VARCHAR(63),
                            exposureMode                             VARCHAR(63),
                         exposureProgram                             VARCHAR(63),
                            exposureTime                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                              fileSource                             VARCHAR(63),
                             focalLength                             VARCHAR(63),
                              gpsVersion                            VARCHAR(255),
                          isoCountryCode                            VARCHAR(255),
                          isoSpeedRating                             VARCHAR(63),
                                latitude                             VARCHAR(63),
                             latitudeRef                            VARCHAR(255),
                                    lens                             VARCHAR(63),
                               longitude                             VARCHAR(63),
                            longitudeRef                            VARCHAR(255),
                                mapDatum                             VARCHAR(63),
                            meteringMode                             VARCHAR(63),
                                province                             VARCHAR(63),
                             subLocation                            VARCHAR(255),
                               timestamp                                DATETIME,
                            whiteBalance                             VARCHAR(63),
                             worldRegion                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                             
insert into description_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, type from node_history where type='CameraData' or type='GeoCoding' or type='IPTC';
update description_history t, property p set t.alternativeTitle = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'alternativeTitle';
update description_history t, property p set t.city = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'city';
update description_history t, property p set t.country = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'country';
update description_history t, property p set t.digitalSourceType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'digitalSourceType';
update description_history t, property p set t.event = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'event';
update description_history t, property p set t.exposureFNumber = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'exposureFNumber';
update description_history t, property p set t.exposureMode = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'exposureMode';
update description_history t, property p set t.exposureProgram = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'exposureProgram';
update description_history t, property p set t.exposureTime = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'exposureTime';
update description_history t, property p set t.fileFormat = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileFormat';
update description_history t, property p set t.fileSource = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'fileSource';
update description_history t, property p set t.focalLength = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'focalLength';
update description_history t, property p set t.gpsVersion = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'gpsVersion';
update description_history t, property p set t.isoCountryCode = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'isoCountryCode';
update description_history t, property p set t.isoSpeedRating = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'isoSpeedRating';
update description_history t, property p set t.latitude = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'latitude';
update description_history t, property p set t.latitudeRef = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'latitudeRef';
update description_history t, property p set t.lens = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'lens';
update description_history t, property p set t.longitude = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'longitude';
update description_history t, property p set t.longitudeRef = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'longitudeRef';
update description_history t, property p set t.mapDatum = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'mapDatum';
update description_history t, property p set t.meteringMode = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'meteringMode';
update description_history t, property p set t.province = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'province';
update description_history t, property p set t.subLocation = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'subLocation';
update description_history t, property p set t.timestamp = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'timestamp';
update description_history t, property p set t.whiteBalance = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'whiteBalance';
update description_history t, property p set t.worldRegion = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'worldRegion';
                             
CREATE INDEX description_history_id ON description_history (id);
CREATE INDEX description_history_txn_id ON description_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS description;
CREATE TABLE IF NOT EXISTS description (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        alternativeTitle                             VARCHAR(63),
                                    city                             VARCHAR(63),
                                 country                            VARCHAR(255),
                       digitalSourceType                            VARCHAR(255),
                                   event                            VARCHAR(255),
                         exposureFNumber                             VARCHAR(63),
                            exposureMode                             VARCHAR(63),
                         exposureProgram                             VARCHAR(63),
                            exposureTime                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                              fileSource                             VARCHAR(63),
                             focalLength                             VARCHAR(63),
                              gpsVersion                            VARCHAR(255),
                          isoCountryCode                            VARCHAR(255),
                          isoSpeedRating                             VARCHAR(63),
                                latitude                             VARCHAR(63),
                             latitudeRef                            VARCHAR(255),
                                    lens                             VARCHAR(63),
                               longitude                             VARCHAR(63),
                            longitudeRef                            VARCHAR(255),
                                mapDatum                             VARCHAR(63),
                            meteringMode                             VARCHAR(63),
                                province                             VARCHAR(63),
                             subLocation                            VARCHAR(255),
                               timestamp                                DATETIME,
                            whiteBalance                             VARCHAR(63),
                             worldRegion                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                             
insert into description select * from description_history where txn_end = 0;

CREATE INDEX description_id ON description (id);
CREATE INDEX description_txn_id ON description (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_description;
CREATE TABLE IF NOT EXISTS sess_description (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                        alternativeTitle                             VARCHAR(63),
                                    city                             VARCHAR(63),
                                 country                            VARCHAR(255),
                       digitalSourceType                            VARCHAR(255),
                                   event                            VARCHAR(255),
                         exposureFNumber                             VARCHAR(63),
                            exposureMode                             VARCHAR(63),
                         exposureProgram                             VARCHAR(63),
                            exposureTime                             VARCHAR(63),
                              fileFormat                             VARCHAR(63),
                              fileSource                             VARCHAR(63),
                             focalLength                             VARCHAR(63),
                              gpsVersion                            VARCHAR(255),
                          isoCountryCode                            VARCHAR(255),
                          isoSpeedRating                             VARCHAR(63),
                                latitude                             VARCHAR(63),
                             latitudeRef                            VARCHAR(255),
                                    lens                             VARCHAR(63),
                               longitude                             VARCHAR(63),
                            longitudeRef                            VARCHAR(255),
                                mapDatum                             VARCHAR(63),
                            meteringMode                             VARCHAR(63),
                                province                             VARCHAR(63),
                             subLocation                            VARCHAR(255),
                               timestamp                                DATETIME,
                            whiteBalance                             VARCHAR(63),
                             worldRegion                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_description_id ON sess_description (id);
CREATE INDEX sess_description_txn_id ON sess_description (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS party_history;
CREATE TABLE IF NOT EXISTS party_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                                  orgUrl                            VARCHAR(255),
                              suppressed                                 BOOLEAN,
                                 logoUrl                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                 
insert into party_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, type from node_history where type='Party';
update party_history t, property p set t.orgUrl = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'orgUrl';
update party_history t, property p set t.suppressed = conv(hex(value),16,10) = 1  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'suppressed';
update party_history t, property p set t.logoUrl = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'logoUrl';
update party_history t, property p set t.name = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'name';


CREATE INDEX party_history_id ON party_history (id);
CREATE INDEX party_history_txn_id ON party_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS party;
CREATE TABLE IF NOT EXISTS party (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                                  orgUrl                            VARCHAR(255),
                              suppressed                                 BOOLEAN,
                                 logoUrl                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                 
insert into party select * from party_history where txn_end = 0;
                                 
CREATE INDEX party_id ON party (id);
CREATE INDEX party_txn_id ON party (id, txn_start, txn_end);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS sess_party;
CREATE TABLE IF NOT EXISTS sess_party (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                                  orgUrl                            VARCHAR(255),
                              suppressed                                 BOOLEAN,
                                 logoUrl                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_party_id ON sess_party (id);
CREATE INDEX sess_party_txn_id ON sess_party (id, txn_start, txn_end);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS tag_history;
CREATE TABLE IF NOT EXISTS tag_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                             description                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

insert into tag_history (id, txn_start, txn_end, type) select id, txn_start, txn_end, type from node_history where type='Tag';
update tag_history t, property p set t.description = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'description';
update tag_history t, property p set t.name = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'name';

                             
                             
CREATE INDEX tag_history_id ON tag_history (id);
CREATE INDEX tag_history_txn_id ON tag_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS tag;
CREATE TABLE IF NOT EXISTS tag (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                             description                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                             
insert into tag select * from tag_history where txn_end = 0;
                             
CREATE INDEX tag_id ON tag (id);
CREATE INDEX tag_txn_id ON tag (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_tag;
CREATE TABLE IF NOT EXISTS sess_tag (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                    type                             VARCHAR(15),

                                    name                            VARCHAR(255),
                             description                            VARCHAR(255)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_tag_id ON sess_tag (id);
CREATE INDEX sess_tag_txn_id ON sess_tag (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS flatedge_history;
CREATE TABLE IF NOT EXISTS flatedge_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                   
insert into flatedge_history (id, txn_start, txn_end, v_out, v_in, label, edge_order) select id, txn_start, txn_end, v_out, v_in, label, edge_order from edge;
                                   
CREATE INDEX flatedge_history_id ON flatedge_history (id);
CREATE INDEX flatedge_history_txn_id ON flatedge_history (id, txn_start, txn_end);
CREATE INDEX flatedge_history_label_vertices ON flatedge_history (label, v_in, v_out);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS flatedge;
CREATE TABLE IF NOT EXISTS flatedge (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
                                   
insert into flatedge select * from flatedge_history where txn_end = 0;

CREATE INDEX flatedge_id ON flatedge (id);
CREATE INDEX flatedge_txn_id ON flatedge (id, txn_start, txn_end);
CREATE INDEX flatedge_label_vertices ON flatedge (label, v_in, v_out);
CREATE INDEX flatedge_label_vout ON flatedge (label, v_out);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS sess_flatedge;
CREATE TABLE IF NOT EXISTS sess_flatedge (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16)) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_flatedge_id ON sess_flatedge (id);
CREATE INDEX sess_flatedge_txn_id ON sess_flatedge (id, txn_start, txn_end);
CREATE INDEX sess_flatedge_label_vertices ON sess_flatedge (label, v_in, v_out);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS acknowledge_history;
CREATE TABLE IF NOT EXISTS acknowledge_history (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16),
                                 ackType                                    TEXT,
                                    date                                DATETIME,
                           kindOfSupport                                    TEXT,
                               weighting                                  DOUBLE,
                           urlToOriginal                                    TEXT) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

insert into acknowledge_history (id, txn_start, txn_end, v_out, v_in, edge_order, label) select id, txn_start, txn_end, v_out, v_in, edge_order, label from flatedge_history where label = 'Acknowledge';
update acknowledge_history t, property p set t.ackType = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'ackType';
update acknowledge_history t, property p set t.date = from_unixtime(conv(hex(value),16,10) / 1000)  where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'date';
update acknowledge_history t, property p set t.kindOfSupport = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'kindOfSupport';
update acknowledge_history t, property p set t.urlToOriginal = convert(p.value using 'utf8mb4') where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'urlToOriginal';
update acknowledge_history t, property p set t.weighting = pow(2,conv(substring(hex(value),1,3),16,10) - 1023) * (1 + conv(substring(hex(value),4,14),16,10)) where t.id = p.id and t.txn_start = p.txn_start and t.txn_end = p.txn_end and p.name = 'weighting'; -- Doesn't handle negative numbers 

CREATE INDEX acknowledge_history_id ON acknowledge_history (id);
CREATE INDEX acknowledge_history_txn_id ON acknowledge_history (id, txn_start, txn_end);

-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------
DROP TABLE IF EXISTS acknowledge;
CREATE TABLE IF NOT EXISTS acknowledge (
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16),
                                 ackType                                    TEXT,
                                    date                                DATETIME,
                           kindOfSupport                                    TEXT,
                               weighting                                  DOUBLE,
                           urlToOriginal                                    TEXT) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

insert into acknowledge select * from acknowledge_history where txn_end = 0;
                           
CREATE INDEX acknowledge_id ON acknowledge (id);
CREATE INDEX acknowledge_txn_id ON acknowledge (id, txn_start, txn_end);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------

DROP TABLE IF EXISTS sess_acknowledge;
CREATE TABLE IF NOT EXISTS sess_acknowledge (
                                    s_id                                  BIGINT,
                                   state                                 CHAR(3),
                                      id                                  BIGINT,
                               txn_start               BIGINT DEFAULT 0 NOT NULL,
                                 txn_end               BIGINT DEFAULT 0 NOT NULL,
                                   v_out                                  BIGINT,
                                    v_in                                  BIGINT,
                              edge_order                                  BIGINT,
                                   label                             VARCHAR(16),
                                 ackType                                    TEXT,
                                    date                                DATETIME,
                           kindOfSupport                                    TEXT,
                               weighting                                  DOUBLE,
                           urlToOriginal                                    TEXT) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE INDEX sess_acknowledge_id ON sess_acknowledge (id);
CREATE INDEX sess_acknowledge_txn_id ON sess_acknowledge (id, txn_start, txn_end);
-- -----------------------------------------------------------------------------------------------------------------------------------------------------------------