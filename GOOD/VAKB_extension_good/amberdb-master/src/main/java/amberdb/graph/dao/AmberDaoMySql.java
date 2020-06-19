package amberdb.graph.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;


public abstract class AmberDaoMySql extends AmberDao {



	@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO node_history (id, txn_start, txn_end, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType) "
			 + "SELECT id, s_id, 0, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType "
			 + "FROM sess_node "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO node_history (id, txn_start, txn_end, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType) "
			 + "SELECT id, s_id, 0, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType "
			 + "FROM sess_node "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO node (id, txn_start, txn_end, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType) "
			 + "SELECT id, s_id, 0, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType "
			 + "FROM sess_node "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE node c, sess_node s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.accessConditions = s.accessConditions, c.alias = s.alias, c.commentsExternal = s.commentsExternal, c.commentsInternal = s.commentsInternal, c.expiryDate = s.expiryDate, c.internalAccessConditions = s.internalAccessConditions, c.localSystemNumber = s.localSystemNumber, c.name = s.name, c.notes = s.notes, c.recordSource = s.recordSource, c.restrictionType = s.restrictionType "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startNodes(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE node_history h, sess_node s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM node c, sess_node s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endNodes(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO work_history (id, txn_start, txn_end, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid) "
			 + "SELECT id, s_id, 0, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid "
			 + "FROM sess_work "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO work_history (id, txn_start, txn_end, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid) "
			 + "SELECT id, s_id, 0, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid "
			 + "FROM sess_work "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO work (id, txn_start, txn_end, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid) "
			 + "SELECT id, s_id, 0, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid "
			 + "FROM sess_work "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE work c, sess_work s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.abstract = s.abstract, c.access = s.access, c.accessAgreement = s.accessAgreement, c.accessConditions = s.accessConditions, c.accessAgreementReasonForChange = s.accessAgreementReasonForChange,c.accessAgreementReasonForChangeSum = s.accessAgreementReasonForChangeSum,c.accessConditionReasonForChange = s.accessConditionReasonForChange,c.accessConditionReasonForChangeSum = s.accessConditionReasonForChangeSum,c.commercialStatusReasonForChange = s.commercialStatusReasonForChange,c.commercialStatusReasonForChangeSum = s.commercialStatusReasonForChangeSum,c.accessComments = s.accessComments,c.acquisitionCategory = s.acquisitionCategory, c.acquisitionStatus = s.acquisitionStatus, c.additionalContributor = s.additionalContributor, c.additionalCreator = s.additionalCreator, c.additionalSeries = s.additionalSeries, c.additionalSeriesStatement = s.additionalSeriesStatement, c.additionalTitle = s.additionalTitle, c.addressee = s.addressee, c.adminInfo = s.adminInfo, c.advertising = s.advertising, c.algorithm = s.algorithm, c.alias = s.alias, c.allowHighResdownload = s.allowHighResdownload, c.allowOnsiteAccess = s.allowOnsiteAccess, c.alternativeTitle = s.alternativeTitle, c.altform = s.altform, c.arrangement = s.arrangement, c.australianContent = s.australianContent, c.bestCopy = s.bestCopy, c.parentBibId = s.parentBibId, c.bibId = s.bibId, c.bibLevel = s.bibLevel, c.bibliography = s.bibliography, c.captions = s.captions, c.carrier = s.carrier, c.category = s.category, c.childRange = s.childRange, c.classification = s.classification, c.collection = s.collection, c.collectionNumber = s.collectionNumber, c.commentsExternal = s.commentsExternal, c.commentsInternal = s.commentsInternal, c.commercialStatus = s.commercialStatus, c.copyCondition = s.copyCondition, c.availabilityConstraint = s.availabilityConstraint, c.contributor = s.contributor, c.coordinates = s.coordinates, c.copyingPublishing = s.copyingPublishing, c.copyrightPolicy = s.copyrightPolicy, c.copyRole = s.copyRole, c.copyStatus = s.copyStatus, c.copyType = s.copyType, c.correspondenceHeader = s.correspondenceHeader, c.correspondenceId = s.correspondenceId, c.correspondenceIndex = s.correspondenceIndex, c.coverage = s.coverage,c.creativeCommons = s.creativeCommons, c.creator = s.creator, c.creatorStatement = s.creatorStatement, c.currentVersion = s.currentVersion, c.dateCreated = s.dateCreated, c.dateRangeInAS = s.dateRangeInAS, c.dcmAltPi = s.dcmAltPi, c.dcmCopyPid = s.dcmCopyPid, c.dcmDateTimeCreated = s.dcmDateTimeCreated, c.dcmDateTimeUpdated = s.dcmDateTimeUpdated, c.dcmRecordCreator = s.dcmRecordCreator, c.dcmRecordUpdater = s.dcmRecordUpdater, c.dcmSourceCopy = s.dcmSourceCopy, c.dcmWorkPid = s.dcmWorkPid, c.depositType = s.depositType, c.digitalStatus = s.digitalStatus, c.digitalStatusDate = s.digitalStatusDate, c.displayTitlePage = s.displayTitlePage, c.eadUpdateReviewRequired = s.eadUpdateReviewRequired, c.edition = s.edition, c.encodingLevel = s.encodingLevel, c.endChild = s.endChild, c.endDate = s.endDate, c.eventNote = s.eventNote, c.exhibition = s.exhibition, c.expiryDate = s.expiryDate, c.extent = s.extent, c.findingAidNote = s.findingAidNote, c.firstPart = s.firstPart, c.folder = s.folder, c.folderNumber = s.folderNumber, c.folderType = s.folderType, c.form = s.form, c.genre = s.genre, c.heading = s.heading, c.holdingId = s.holdingId, c.holdingNumber = s.holdingNumber, c.html = s.html, c.illustrated = s.illustrated, c.ilmsSentDateTime = s.ilmsSentDateTime, c.immutable = s.immutable, c.ingestJobId = s.ingestJobId, c.interactiveIndexAvailable = s.interactiveIndexAvailable, c.internalAccessConditions = s.internalAccessConditions, c.isMissingPage = s.isMissingPage, c.issn = s.issn, c.issueDate = s.issueDate, c.language = s.language, c.localSystemNumber = s.localSystemNumber, c.manipulation = s.manipulation, c.materialFromMultipleSources = s.materialFromMultipleSources, c.materialType = s.materialType, c.metsId = s.metsId, c.moreIlmsDetailsRequired = s.moreIlmsDetailsRequired, c.notes = s.notes, c.occupation = s.occupation, c.otherNumbers = s.otherNumbers, c.otherTitle = s.otherTitle, c.parentConstraint = s.parentConstraint, c.preferredCitation = s.preferredCitation, c.preservicaId = s.preservicaId, c.preservicaType = s.preservicaType, c.printedPageNumber = s.printedPageNumber, c.provenance = s.provenance, c.publicationCategory = s.publicationCategory, c.publicationLevel = s.publicationLevel, c.publicNotes = s.publicNotes, c.publisher = s.publisher, c.rdsAcknowledgementReceiver = s.rdsAcknowledgementReceiver, c.rdsAcknowledgementType = s.rdsAcknowledgementType, c.recordSource = s.recordSource, c.relatedMaterial = s.relatedMaterial, c.repository = s.repository, c.representativeId = s.representativeId, c.restrictionsOnAccess = s.restrictionsOnAccess, c.restrictionType = s.restrictionType, c.rights = s.rights, c.scaleEtc = s.scaleEtc, c.scopeContent = s.scopeContent, c.segmentIndicator = s.segmentIndicator, c.sendToIlms = s.sendToIlms, c.sendToIlmsDateTime = s.sendToIlmsDateTime, c.sensitiveMaterial = s.sensitiveMaterial, c.sensitiveReason = s.sensitiveReason, c.series = s.series, c.sheetCreationDate = s.sheetCreationDate, c.sheetName = s.sheetName, c.standardId = s.standardId, c.startChild = s.startChild, c.startDate = s.startDate, c.subHeadings = s.subHeadings, c.subject = s.subject, c.subType = s.subType, c.subUnitNo = s.subUnitNo, c.subUnitType = s.subUnitType, c.summary = s.summary, c.tempHolding = s.tempHolding, c.tilePosition = s.tilePosition, c.timedStatus = s.timedStatus, c.title = s.title, c.totalDuration = s.totalDuration, c.uniformTitle = s.uniformTitle, c.vendorId = s.vendorId, c.versionNumber = s.versionNumber, c.workCreatedDuringMigration = s.workCreatedDuringMigration, c.workPid = s.workPid "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startWorks(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE work_history h, sess_work s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM work c, sess_work s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endWorks(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO file_history (id, txn_start, txn_end, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel) "
			 + "SELECT id, s_id, 0, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel "
			 + "FROM sess_file "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO file_history (id, txn_start, txn_end, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel) "
			 + "SELECT id, s_id, 0, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel "
			 + "FROM sess_file "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO file (id, txn_start, txn_end, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel) "
			 + "SELECT id, s_id, 0, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel "
			 + "FROM sess_file "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE file c, sess_file s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.application = s.application, c.applicationDateCreated = s.applicationDateCreated, c.bitDepth = s.bitDepth, c.bitrate = s.bitrate, c.blobId = s.blobId, c.blockAlign = s.blockAlign, c.brand = s.brand, c.carrierCapacity = s.carrierCapacity, c.channel = s.channel, c.checksum = s.checksum, c.checksumGenerationDate = s.checksumGenerationDate, c.checksumType = s.checksumType, c.codec = s.codec, c.colourProfile = s.colourProfile, c.colourSpace = s.colourSpace, c.compression = s.compression, c.cpLocation = s.cpLocation, c.dateDigitised = s.dateDigitised, c.dcmCopyPid = s.dcmCopyPid, c.device = s.device, c.deviceSerialNumber = s.deviceSerialNumber, c.duration = s.duration, c.durationType = s.durationType, c.encoding = s.encoding, c.equalisation = s.equalisation, c.fileContainer = s.fileContainer, c.fileFormat = s.fileFormat, c.fileFormatVersion = s.fileFormatVersion, c.fileName = s.fileName, c.fileSize = s.fileSize, c.framerate = s.framerate, c.imageLength = s.imageLength, c.imageWidth = s.imageWidth, c.location = s.location, c.manufacturerMake = s.manufacturerMake, c.manufacturerModelName = s.manufacturerModelName, c.manufacturerSerialNumber = s.manufacturerSerialNumber, c.mimeType = s.mimeType, c.notes = s.notes, c.orientation = s.orientation, c.photometric = s.photometric, c.reelSize = s.reelSize, c.resolution = s.resolution, c.resolutionUnit = s.resolutionUnit, c.samplesPerPixel = s.samplesPerPixel, c.samplingRate = s.samplingRate, c.software = s.software, c.softwareSerialNumber = s.softwareSerialNumber, c.soundField = s.soundField, c.speed = s.speed, c.surface = s.surface, c.thickness = s.thickness, c.toolId = s.toolId, c.zoomLevel = s.zoomLevel "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startFiles(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE file_history h, sess_file s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM file c, sess_file s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endFiles(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO description_history (id, txn_start, txn_end, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion) "
			 + "SELECT id, s_id, 0, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion "
			 + "FROM sess_description "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO description_history (id, txn_start, txn_end, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion) "
			 + "SELECT id, s_id, 0, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion "
			 + "FROM sess_description "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO description (id, txn_start, txn_end, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion) "
			 + "SELECT id, s_id, 0, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion "
			 + "FROM sess_description "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE description c, sess_description s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.alternativeTitle = s.alternativeTitle, c.city = s.city, c.country = s.country, c.digitalSourceType = s.digitalSourceType, c.event = s.event, c.exposureFNumber = s.exposureFNumber, c.exposureMode = s.exposureMode, c.exposureProgram = s.exposureProgram, c.exposureTime = s.exposureTime, c.fileFormat = s.fileFormat, c.fileSource = s.fileSource, c.focalLength = s.focalLength, c.gpsVersion = s.gpsVersion, c.isoCountryCode = s.isoCountryCode, c.isoSpeedRating = s.isoSpeedRating, c.latitude = s.latitude, c.latitudeRef = s.latitudeRef, c.lens = s.lens, c.longitude = s.longitude, c.longitudeRef = s.longitudeRef, c.mapDatum = s.mapDatum, c.meteringMode = s.meteringMode, c.province = s.province, c.subLocation = s.subLocation, c.timestamp = s.timestamp, c.whiteBalance = s.whiteBalance, c.worldRegion = s.worldRegion "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startDescriptions(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE description_history h, sess_description s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM description c, sess_description s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endDescriptions(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO party_history (id, txn_start, txn_end, type,name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type,name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO party_history (id, txn_start, txn_end, type,name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type,name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO party (id, txn_start, txn_end, type,name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type,name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE party c, sess_party s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.name = s.name, c.orgUrl = s.orgUrl, c.suppressed = s.suppressed, c.logoUrl = s.logoUrl "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startParties(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE party_history h, sess_party s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM party c, sess_party s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endParties(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO tag_history (id, txn_start, txn_end, type, name, description) "
			 + "SELECT id, s_id, 0, type, name, description "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO tag_history (id, txn_start, txn_end, type, name, description) "
			 + "SELECT id, s_id, 0, type, name, description "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO tag (id, txn_start, txn_end, type, name, description) "
			 + "SELECT id, s_id, 0, type, name, description "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE tag c, sess_tag s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.type = s.type, c.name = s.name, c.description = s.description "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startTags(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE tag_history h, sess_tag s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM tag c, sess_tag s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endTags(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "REPLACE INTO flatedge_history (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "REPLACE INTO flatedge_history (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "REPLACE INTO flatedge (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE flatedge c, sess_flatedge s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.label = s.label, c.v_out = s.v_out, c.v_in = s.v_in, c.edge_order = s.edge_order "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startFlatedges(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE flatedge_history h, sess_flatedge s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM flatedge c, sess_flatedge s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endFlatedges(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "REPLACE INTO acknowledge_history (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "REPLACE INTO acknowledge_history (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "REPLACE INTO acknowledge (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "UPDATE acknowledge c, sess_acknowledge s "
			 + "SET c.txn_start = s.s_id, c.txn_end = s.txn_end, c.v_out = s.v_out, c.v_in = s.v_in, c.edge_order = s.edge_order, c.ackType = s.ackType, c.date = s.date, c.kindOfSupport = s.kindOfSupport, c.weighting = s.weighting, c.urlToOriginal = s.urlToOriginal "
			 + "WHERE c.id = s.id "
			 + "AND s_id = @txn "
			 + "AND state = 'MOD';")
			public abstract void startAcknowledgements(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE acknowledge_history h, sess_acknowledge s "
			 + "SET h.txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state <> 'AMB'; "
			 + " "
			 + "DELETE c "
			 + "FROM acknowledge c, sess_acknowledge s "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id = s.id "
			 + "AND s.s_id = @txn "
			 + "AND s.state in ('DEL', 'NEW');")
			public abstract void endAcknowledgements(
			@Bind("txnId") Long txnId);

}

