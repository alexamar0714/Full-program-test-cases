package amberdb.graph.dao;


import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;

public abstract class AmberDaoH2 extends AmberDao {


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

			 + "DELETE from node WHERE id in (SELECT id from sess_node WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO node (id, txn_start, txn_end, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType) "
			 + "SELECT id, s_id, 0, type, accessConditions,alias,commentsExternal,commentsInternal,expiryDate,internalAccessConditions,localSystemNumber,name,notes,recordSource,restrictionType "
			 + "FROM sess_node "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")

			public abstract void startNodes(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE node_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_node WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM node c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_node WHERE s_id = @txn AND STATE = 'DEL');")
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

			 + "DELETE from work WHERE id in (SELECT id from sess_work WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO work (id, txn_start, txn_end, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid) "
			 + "SELECT id, s_id, 0, type, abstract,access,accessAgreement,accessConditions,accessAgreementReasonForChange,accessAgreementReasonForChangeSum,accessConditionReasonForChange,accessConditionReasonForChangeSum,commercialStatusReasonForChange,commercialStatusReasonForChangeSum,accessComments,acquisitionCategory,acquisitionStatus,additionalContributor,additionalCreator,additionalSeries,additionalSeriesStatement,additionalTitle,addressee,adminInfo,advertising,algorithm,alias,allowHighResdownload,allowOnsiteAccess,alternativeTitle,altform,arrangement,australianContent,bestCopy,parentBibId,bibId,bibLevel,bibliography,captions,carrier,category,childRange,classification,collection,collectionNumber,commentsExternal,commentsInternal,commercialStatus,copyCondition,availabilityConstraint,contributor,coordinates,copyingPublishing,copyrightPolicy,copyRole,copyStatus,copyType,correspondenceHeader,correspondenceId,correspondenceIndex,coverage,creativeCommons,creator,creatorStatement,currentVersion,dateCreated,dateRangeInAS,dcmAltPi,dcmCopyPid,dcmDateTimeCreated,dcmDateTimeUpdated,dcmRecordCreator,dcmRecordUpdater,dcmSourceCopy,dcmWorkPid,depositType,digitalStatus,digitalStatusDate,displayTitlePage,eadUpdateReviewRequired,edition,encodingLevel,endChild,endDate,eventNote,exhibition,expiryDate,extent,findingAidNote,firstPart,folder,folderNumber,folderType,form,genre,heading,holdingId,holdingNumber,html,illustrated,ilmsSentDateTime,immutable,ingestJobId,interactiveIndexAvailable,internalAccessConditions,isMissingPage,issn,issueDate,language,localSystemNumber,manipulation,materialFromMultipleSources,materialType,metsId,moreIlmsDetailsRequired,notes,occupation,otherNumbers,otherTitle,parentConstraint,preferredCitation,preservicaId,preservicaType,printedPageNumber,provenance,publicationCategory,publicationLevel,publicNotes,publisher,rdsAcknowledgementReceiver,rdsAcknowledgementType,recordSource,relatedMaterial,repository,representativeId,restrictionsOnAccess,restrictionType,rights,scaleEtc,scopeContent,segmentIndicator,sendToIlms,sendToIlmsDateTime,sensitiveMaterial,sensitiveReason,series,sheetCreationDate,sheetName,standardId,startChild,startDate,subHeadings,subject,subType,subUnitNo,subUnitType,summary,tempHolding,tilePosition,timedStatus,title,totalDuration,uniformTitle,vendorId,versionNumber,workCreatedDuringMigration,workPid "
			 + "FROM sess_work "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")
			public abstract void startWorks(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE work_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_work WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM work c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_work WHERE s_id = @txn AND STATE = 'DEL');")
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

			 + "DELETE from file WHERE id in (SELECT id from sess_file WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO file (id, txn_start, txn_end, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel) "
			 + "SELECT id, s_id, 0, type, application,applicationDateCreated,bitDepth,bitrate,blobId,blockAlign,brand,carrierCapacity,channel,checksum,checksumGenerationDate,checksumType,codec,colourProfile,colourSpace,compression,cpLocation,dateDigitised,dcmCopyPid,device,deviceSerialNumber,duration,durationType,encoding,equalisation,fileContainer,fileFormat,fileFormatVersion,fileName,fileSize,framerate,imageLength,imageWidth,location,manufacturerMake,manufacturerModelName,manufacturerSerialNumber,mimeType,notes,orientation,photometric,reelSize,resolution,resolutionUnit,samplesPerPixel,samplingRate,software,softwareSerialNumber,soundField,speed,surface,thickness,toolId,zoomLevel "
			 + "FROM sess_file "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")
			public abstract void startFiles(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE file_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_file WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM file c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_file WHERE s_id = @txn AND STATE = 'DEL');")
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

			 + "DELETE from description WHERE id in (SELECT id from sess_description WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO description (id, txn_start, txn_end, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion) "
			 + "SELECT id, s_id, 0, type, alternativeTitle,city,country,digitalSourceType,event,exposureFNumber,exposureMode,exposureProgram,exposureTime,fileFormat,fileSource,focalLength,gpsVersion,isoCountryCode,isoSpeedRating,latitude,latitudeRef,lens,longitude,longitudeRef,mapDatum,meteringMode,province,subLocation,timestamp,whiteBalance,worldRegion "
			 + "FROM sess_description "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")
			public abstract void startDescriptions(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE description_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_description WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM description c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_description WHERE s_id = @txn AND STATE = 'DEL');")
			public abstract void endDescriptions(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO party_history (id, txn_start, txn_end, type, name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type, name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO party_history (id, txn_start, txn_end, type, name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type, name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "DELETE from party WHERE id in (SELECT id from sess_party WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO party (id, txn_start, txn_end, type, name,orgUrl,suppressed,logoUrl) "
			 + "SELECT id, s_id, 0, type, name,orgUrl,suppressed,logoUrl "
			 + "FROM sess_party "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")
			public abstract void startParties(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE party_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_party WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM party c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_party WHERE s_id = @txn AND STATE = 'DEL');")
			public abstract void endParties(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO tag_history (id, txn_start, txn_end, type, description, name) "
			 + "SELECT id, s_id, 0, type, description, name "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO tag_history (id, txn_start, txn_end, type, description, name) "
			 + "SELECT id, s_id, 0, type, description, name "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "
			 
			 + "DELETE from tag WHERE id in (SELECT id from sess_tag WHERE s_id = @txn AND state = 'MOD');"

			 + "INSERT INTO tag (id, txn_start, txn_end, type, description, name) "
			 + "SELECT id, s_id, 0, type, description, name "
			 + "FROM sess_tag "
			 + "WHERE s_id = @txn "
			 + "AND (state = 'NEW' or state = 'MOD'); ")
			public abstract void startTags(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE tag_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_tag WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM tag c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_tag WHERE s_id = @txn AND STATE = 'DEL');")
			public abstract void endTags(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO flatedge_history (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO flatedge_history (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO flatedge (id, txn_start, txn_end, label, v_out,v_in,edge_order) "
			 + "SELECT distinct id, s_id, 0, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "MERGE INTO flatedge (id, txn_start, txn_end, label, v_out,v_in,edge_order) key(id)"
			 + "SELECT id, s_id, txn_end, label, v_out,v_in,edge_order "
			 + "FROM sess_flatedge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; ")
			public abstract void startFlatedges(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE flatedge_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_flatedge WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM flatedge c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_flatedge WHERE s_id = @txn AND STATE = 'DEL');")
			public abstract void endFlatedges(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "INSERT INTO acknowledge_history (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "INSERT INTO acknowledge_history (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; "

			 + "INSERT INTO acknowledge (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) "
			 + "SELECT id, s_id, 0, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'NEW'; "

			 + "MERGE INTO acknowledge (id, txn_start, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal) key(id)"
			 + "SELECT id, s_id, txn_end, v_out,v_in,edge_order,ackType,date,kindOfSupport,weighting,urlToOriginal "
			 + "FROM sess_acknowledge "
			 + "WHERE s_id = @txn "
			 + "AND state = 'MOD'; ")
			public abstract void startAcknowledgements(
			@Bind("txnId") Long txnId);

			@SqlUpdate("SET @txn = :txnId;"
			 + "UPDATE acknowledge_history h "
			 + "SET txn_end = @txn "
			 + "WHERE h.txn_end = 0 "
			 + "AND h.id IN (SELECT id FROM sess_acknowledge WHERE s_id = @txn AND STATE <> 'AMB');"
			 + " "
			 + "DELETE FROM acknowledge c "
			 + "WHERE c.txn_end = 0 "
			 + "AND c.id IN (SELECT id FROM sess_acknowledge WHERE s_id = @txn AND STATE = 'DEL');")
			public abstract void endAcknowledgements(
			@Bind("txnId") Long txnId);

}

