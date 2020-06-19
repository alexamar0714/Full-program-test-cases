package amberdb.model;

import amberdb.AmberSession;
import amberdb.DataIntegrityException;
import amberdb.InvalidSubtypeException;
import amberdb.enums.CopyRole;
import amberdb.enums.CopyType;
import amberdb.enums.SubType;
import amberdb.graph.AmberGraph;
import amberdb.graph.AmberQuery;
import amberdb.graph.AmberVertex;
import amberdb.relation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.blueprints.util.wrappers.wrapped.WrappedVertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Incidence;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.annotations.gremlin.GremlinGroovy;
import com.tinkerpop.frames.annotations.gremlin.GremlinParam;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static amberdb.graph.BranchType.BRANCH_FROM_ALL;
import static amberdb.graph.BranchType.BRANCH_FROM_PREVIOUS;
import static org.apache.commons.lang3.StringUtils.containsIgnoreCase;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

/**
 * Any logical work that is collected or created by the library such as a book,
 * page, map, physical object or sound recording.
 *
 * A complex digital object may be made up of multiple related works forming a
 * graph. All works in a single digital object belong to a parent-child tree
 * formed by the {@link IsPartOf} relationship.
 *
 * @see {@link Copy}
 */
@TypeValue("Work")
public interface Work extends Node {
    @Property("abstract")
    String getAbstract();

    @Property("abstract")
    void setAbstract(String aBstract);

    @Property("category")
    String getCategory();

    @Property("category")
    void setCategory(String category);

    /* DCM Legacy Data */

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getDcmAltPi to get this property
     */
    @Property("dcmAltPi")
    String getJSONDcmAltPi();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setDcmAltPi to set this property
     */
    @Property("dcmAltPi")
    void setJSONDcmAltPi(String dcmAltPi);

    /**
     * This method handles the JSON serialisation of the dcmAltPi Property
     */
    @JavaHandler
    void setDcmAltPi(List<String> list) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the dcmAltPi Property
     */
    @JavaHandler
    List<String> getDcmAltPi();

    @Property("dcmWorkPid")
    String getDcmWorkPid();

    @Property("dcmWorkPid")
    void setDcmWorkPid(String dcmWorkPid);

    @Property("dcmDateTimeCreated")
    Date getDcmDateTimeCreated();

    @Property("dcmDateTimeCreated")
    void setDcmDateTimeCreated(Date dcmDateTimeCreated);

    @Property("dcmDateTimeUpdated")
    Date getDcmDateTimeUpdated();

    @Property("dcmDateTimeUpdated")
    void setDcmDateTimeUpdated(Date dcmDateTimeUpd);

    @Property("dcmRecordCreator")
    String getDcmRecordCreator();

    @Property("dcmRecordCreator")
    void setDcmRecordCreator(String dcmRecordCreator);

    @Property("dcmRecordUpdater")
    String getDcmRecordUpdater();

    @Property("dcmRecordUpdater")
    void setDcmRecordUpdater(String dcmRecordUpdater);

    /* END DCM Legacy Data */

    @Property("subUnitType")
    String getSubUnitType();

    @Property("subUnitType")
    void setSubUnitType(String subUnitType);

    @Property("subUnitNo")
    String getSubUnitNo();

    @Property("subUnitNo")
    void setSubUnitNo(String subUnitNo);

    @Property("subType")
    String getSubType();

    @Property("subType")
    void setSubType(String subType);

    @Property("issueDate")
    Date getIssueDate();

    @Property("issueDate")
    void setIssueDate(Date issueDate);

    @Property("collection")
    String getCollection();

    @Property("collection")
    void setCollection(String collection);

    @Property("depositType")
    String getDepositType();

    @Property("depositType")
    void setDepositType(String depositType);

    @Property("form")
    String getForm();

    @Property("form")
    void setForm(String form);

    @Property("displayTitlePage")
    Boolean isDisplayTitlePage();

    @Property("displayTitlePage")
    void setDisplayTitlePage(Boolean displayTitlePage);

    @Property("bibLevel")
    String getBibLevel();

    @Property("bibLevel")
    void setBibLevel(String bibLevel);

    @Property("digitalStatus")
    String getDigitalStatus();

    @Property("digitalStatus")
    void setDigitalStatus(String digitalStatus);

    @Property("digitalStatusDate")
    Date getDigitalStatusDate();

    @Property("digitalStatusDate")
    void setDigitalStatusDate(Date digitalStatusDate);

    @Property("heading")
    String getHeading();

    @Property("heading")
    void setHeading(String heading);

    @Property("subHeadings")
    String getSubHeadings();

    @Property("subHeadings")
    void setSubHeadings(String subHeadings);

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    String getHoldingNumber();

    /**
     * Also known as CALLNO
     */
    @Property("holdingNumber")
    void setHoldingNumber(String holdingNumber);

    @Property("holdingId")
    String getHoldingId();

    /**
     * Also known as CALLNO
     */
    @Property("holdingId")
    void setHoldingId(String holdingId);

    @JavaHandler
    String getHoldingNumberAndId();

    @JavaHandler
    void setHoldingNumberAndId(String holdNumAndId);

    @Property("issn")
    String getISSN();

    @Property("issn")
    void setISSN(String issn);

    @Property("title")
    String getTitle();

    @Property("title")
    void setTitle(String title);

    @Property("creativeCommons")
    String getCreativeCommons();

    @Property("creativeCommons")
    void setCreativeCommons(String creativeCommons);

    @Property("creator")
    String getCreator();

    @Property("creator")
    void setCreator(String creator);

    @Property("creatorStatement")
    String getCreatorStatement();

    @Property("creatorStatement")
    void setCreatorStatement(String creatorStatement);

    @Property("publisher")
    String getPublisher();

    @Property("publisher")
    void setPublisher(String publisher);

    @Property("copyrightPolicy")
    String getCopyrightPolicy();

    @Property("copyrightPolicy")
    void setCopyrightPolicy(String copyrightPolicy);
   
    @Property("commercialStatus")
    String getCommercialStatus();

    @Property("commercialStatus")
    void setCommercialStatus(String commercialStatus);

    @Property("accessAgreement")
    String getAccessAgreement();

    @Property("accessAgreement")
    void setAccessAgreement(String accessAgreement);

    @Property("commercialStatusReasonForChange")
    String getCommercialStatusReasonForChange();

    @Property("commercialStatusReasonForChange")
    void setCommercialStatusReasonForChange(String commercialStatusReasonForChange);

    @Property("commercialStatusReasonForChangeSum")
    String getCommercialStatusReasonForChangeSum();

    @Property("commercialStatusReasonForChangeSum")
    void setCommercialStatusReasonForChangeSum(String commercialStatusReasonForChangeSum);

    @Property("accessAgreementReasonForChange")
    String getAccessAgreementReasonForChange();

    @Property("accessAgreementReasonForChange")
    void setAccessAgreementReasonForChange(String accessAgreementReasonForChange);

    @Property("accessAgreementReasonForChangeSum")
    String getAccessAgreementReasonForChangeSum();

    @Property("accessAgreementReasonForChangeSum")
    void setAccessAgreementReasonForChangeSum(String accessAgreementReasonForChangeSum);

    @Property("accessConditionReasonForChange")
    String getAccessConditionReasonForChange();

    @Property("accessConditionReasonForChange")
    void setAccessConditionReasonForChange(String accessConditionReasonForChange);

    @Property("accessConditionReasonForChangeSum")
    String getAccessConditionReasonForChangeSum();

    @Property("accessConditionReasonForChangeSum")
    void setAccessConditionReasonForChangeSum(String accessConditionReasonForChangeSum);

    @Property("firstPart")
    @Deprecated
    String getFirstPart();

    @Property("firstPart")
    @Deprecated
    void setFirstPart(String firstPart);

    @Property("sortIndex")
    @Deprecated
    String getSortIndex();

    @Property("sortIndex")
    @Deprecated
    void setSortIndex(String sortIndex);

    @Property("edition")
    String getEdition();

    @Property("edition")
    void setEdition(String edition);

    @Property("immutable")
    String getImmutable();

    @Property("immutable")
    void setImmutable(String immutable);

    @Property("startDate")
    Date getStartDate();

    @Property("startDate")
    void setStartDate(Date startDate);

    @Property("endDate")
    Date getEndDate();

    @Property("endDate")
    void setEndDate(Date endDate);

    @Property("extent")
    String getExtent();

    @Property("extent")
    void setExtent(String extent);

    @Property("language")
    String getLanguage();

    @Property("language")
    void setLanguage(String language);

    @Property("addressee")
    String getAddressee();

    @Property("addressee")
    void setAddressee(String addressee);

    @Property("childRange")
    String getChildRange();

    @Property("childRange")
    void setChildRange(String childRange);

    @Property("startChild")
    String getStartChild();

    @Property("startChild")
    void setStartChild(String startChild);

    @Property("endChild")
    String getEndChild();

    @Property("endChild")
    void setEndChild(String endChild);

    @Property("encodingLevel")
    String getEncodingLevel();

    @Property("encodingLevel")
    void setEncodingLevel(String encodingLevel);

    @Property("publicationLevel")
    String getPublicationLevel();

    @Property("publicationLevel")
    void setPublicationLevel(String publicationLevel);

    @Property("genre")
    String getGenre();

    @Property("genre")
    void setGenre(String genre);

    @Property("publicationCategory")
    String getPublicationCategory();

    @Property("publicationCategory")
    void setPublicationCategory(String publicationCategory);

    @Property("sendToIlms")
    Boolean getSendToIlms();

    @Property("sendToIlms")
    void setSendToIlms(Boolean sendToIlms);

    @Property("ingestJobId")
    Long getIngestJobId();

    @Property("ingestJobId")
    void setIngestJobId(Long ingestJobId);

    @Property("moreIlmsDetailsRequired")
    Boolean getMoreIlmsDetailsRequired();

    @Property("moreIlmsDetailsRequired")
    void setMoreIlmsDetailsRequired(Boolean moreIlmsDetailsRequired);

    @Property("allowHighResdownload")
    Boolean getAllowHighResdownload();

    @Property("allowHighResdownload")
    void setAllowHighResdownload(Boolean allowHRdownload);

    @Property("ilmsSentDateTime")
    Date getIlmsSentDateTime();

    @Property("ilmsSentDateTime")
    void setIlmsSentDateTime(Date dateTime);

    @Property("interactiveIndexAvailable")
    Boolean getInteractiveIndexAvailable();

    @Property("interactiveIndexAvailable")
    void setInteractiveIndexAvailable(Boolean interactiveIndexAvailable);

    @Property("html")
    String getHtml();

    @Property("html")
    void setHtml(String html);

    @Property("isMissingPage")
    Boolean getIsMissingPage();

    @Property("isMissingPage")
    void setIsMissingPage(Boolean isMissingPage);

    @Property("workCreatedDuringMigration")
    Boolean getWorkCreatedDuringMigration();

    @Property("workCreatedDuringMigration")
    void setWorkCreatedDuringMigration(Boolean workCreatedDuringMigration);

    @Property("additionalSeriesStatement")
    String getAdditionalSeriesStatement();

    @Property("additionalSeriesStatement")
    void setAdditionalSeriesStatement(String additionalSeriesStatement);

    @Property("sheetName")
    String getSheetName();

    @Property("sheetName")
    void setSheetName(String sheetName);

    @Property("sheetCreationDate")
    String getSheetCreationDate();

    @Property("sheetCreationDate")
    void setSheetCreationDate(String sheetCreationDate);

    /**
     * Get the vendor identifier that was assigned by the E-Deposit app.
     */
    @Property("vendorId")
    String getVendorId();

    /**
     * Set the vendor identifier that was assigned by the E-Deposit app.
     */
    @Property("vendorId")
    void setVendorId(String id);

    @Property("totalDuration")
    String getTotalDuration();

    @Property("totalDuration")
    void setTotalDuration(String totalDuration);
    
    @Property("preservicaType")
    String getPreservicaType();

    @Property("preservicaType")
    void setPreservicaType(String type);
    
    @Property("preservicaId")
    String getPreservicaId();

    @Property("preservicaId")
    void setPreservicaId(String id);
    
    /**
     * If true, access to the work is allowed within the NLA reading rooms through a restricted method.
     */
    @Property("allowOnsiteAccess")
    Boolean getAllowOnsiteAccess();

    @Property("allowOnsiteAccess")
    void setAllowOnsiteAccess(Boolean allow);
    
    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    GeoCoding addGeoCoding();

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    IPTC addIPTC();

    @JavaHandler
    GeoCoding getGeoCoding();

    @JavaHandler
    IPTC getIPTC();

    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    Acknowledge addAcknowledgement(final Party party);

    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    void removeAcknowledgement(final Acknowledge ack);

    @Incidence(label = Acknowledge.label, direction = Direction.OUT)
    Iterable<Acknowledge> getAcknowledgements();

    @JavaHandler
    Acknowledge addAcknowledgement(final Party party, final String ackType, final String kindOfSupport,
            final Double weighting, final Date dateOfAck, final String urlToOriginal);

    @JavaHandler
    List<Acknowledge> getOrderedAcknowledgements();
    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSeries to get this property
     *
     * NOTE: this property should not be used to retrieve manuscript series
     *       from EAD.  For EAD related work properties, please refer to
     *       amberdb.model.EADWork class.
     */
    @Property("series")
    String getJSONSeries();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSeries to set this property
     *
     * NOTE: this property should not be used to populate manuscript series
     *       from EAD. For EAD related work properties, please refer to
     *       amberdb.model.EADWork class.
     */
    @Property("series")
    void setJSONSeries(String series);

    /**
     * This method handles the JSON serialisation of the series Property
     *
     * NOTE: this property should not be used to populate manuscript series
     *       from EAD. For EAD related work properties, please refer to
     *       amberdb.model.EADWork class.
     */
    @JavaHandler
    void setSeries(List<String> series) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the series Property
     *
     * NOTE: this property should not be used to retrieve manuscript series
     *       from EAD.  For EAD related work properties, please refer to
     *       amberdb.model.EADWork class.
     */
    @JavaHandler
    List<String> getSeries();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getClassification to get this property
     */
    @Property("classification")
    String getJSONClassification();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setClassification to set this property
     */
    @Property("classification")
    void setJSONClassification(String classification);

    /**
     * This method handles the JSON serialisation of the classification Property
     *
     */
    @JavaHandler
    void setClassification(List<String> classification) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the classification
     * Property
     */
    @JavaHandler
    List<String> getClassification();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getContributor to get this property
     */
    @Property("contributor")
    String getJSONContributor();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setContributor to set this property
     */
    @Property("contributor")
    void setJSONContributor(String contributor);

    /**
     * This method handles the JSON serialisation of the contributor Property
     *
     */
    @JavaHandler
    void setContributor(List<String> contributor) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the contributor Property
     *
     */
    @JavaHandler
    List<String> getContributor();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getCoverage to get this property
     */
    @Property("coverage")
    String getJSONCoverage();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setCoverage to set this property
     */
    @Property("coverage")
    void setJSONCoverage(String coverage);

    /**
     * This method handles the JSON serialisation of the coverage Property
     *
     */
    @JavaHandler
    void setCoverage(List<String> coverage) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the coverage Property
     *
     */
    @JavaHandler
    List<String> getCoverage();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOccupation to get this property
     */
    @Property("occupation")
    String getJSONOccupation();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOccupation to set this property
     */
    @Property("occupation")
    void setJSONOccupation(String occupation);

    /**
     * This method handles the JSON serialisation of the occupation Property
     *
     */
    @JavaHandler
    void setOccupation(List<String> occupation) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the occupation Property
     *
     */
    @JavaHandler
    List<String> getOccupation();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getOtherTitle to get this property
     */
    @Property("otherTitle")
    String getJSONOtherTitle();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setOtherTitle to set this property
     */
    @Property("otherTitle")
    void setJSONOtherTitle(String otherTitle);

    /**
     * This method handles the JSON serialisation of the otherTitle Property
     *
     */
    @JavaHandler
    void setOtherTitle(List<String> otherTitle) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the otherTitle Property
     *
     */
    @JavaHandler
    List<String> getOtherTitle();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getStandardId to get this property
     */
    @Property("standardId")
    String getJSONStandardId();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setStandardId to set this property
     */
    @Property("standardId")
    void setJSONStandardId(String standardId);

    /**
     * This method handles the JSON serialisation of the standardId Property
     *
     */
    @JavaHandler
    void setStandardId(List<String> standardId) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the standardId Property
     *
     */
    @JavaHandler
    List<String> getStandardId();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getSubject to get this property
     */
    @Property("subject")
    String getJSONSubject();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setSubject to set this property
     */
    @Property("subject")
    void setJSONSubject(String subject);

    /**
     * This method handles the JSON serialisation of the subject Property
     *
     */
    @JavaHandler
    void setSubject(List<String> subject) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the subject Property
     *
     */
    @JavaHandler
    List<String> getSubject();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getScaleEtc to get this property
     */
    @Property("scaleEtc")
    String getJSONScaleEtc();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setScaleEtc to set this property
     */
    @Property("scaleEtc")
    void setJSONScaleEtc(String scaleEtc);

    /**
     * This method handles the JSON serialisation of the scaleEtc Property
     *
     */
    @JavaHandler
    void setScaleEtc(List<String> scaleEtc) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the scaleEtc Property
     *
     */
    @JavaHandler
    List<String> getScaleEtc();

    @Property("tilePosition")
    String getTilePosition();

    @Property("tilePosition")
    void setTilePosition(String tilePosition);

    @Property("accessComments")
    String getAccessComments();

    @Property("accessComments")
    String setAccessComments(String accessComments);

    @Property("workPid")
    String getWorkPid();

    @Property("workPid")
    void setWorkPid(String workPid);

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getConstraint to get this property
     */
    @Property("constraint")
    String getJSONConstraint();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setConstraint to set this property
     */
    @Property("constraint")
    void setJSONConstraint(String constraint);

    /**
     * This method handles the JSON serialisation of the constraint Property
     *
     */
    @JavaHandler
    void setConstraint(Set<String> constraint) throws JsonProcessingException;

    /**
     * This method handles the JSON deserialisation of the constraint Property
     *
     */
    @JavaHandler
    Set<String> getConstraint();

    @Property("rights")
    String getRights();

    @Property("rights")
    void setRights(String rights);

    @Property("tempHolding")
    String getTempHolding();

    @Property("tempHolding")
    void setTempHolding(String tempHolding);

    @Property("sensitiveMaterial")
    String getSensitiveMaterial();

    @Property("sensitiveMaterial")
    void setSensitiveMaterial(String sensitiveMaterial);

    @Property("sensitiveReason")
    String getJSONSensitiveReason();

    @Property("sensitiveReason")
    void setJSONSensitiveReason(String sensitiveReason);

    @JavaHandler
    void setSensitiveReason(List<String> sensitiveReason) throws JsonProcessingException;

    @JavaHandler
    List<String> getSensitiveReason();

    @Property("restrictionsOnAccess")
    String getJSONRestrictionsOnAccess();

    @Property("restrictionsOnAccess")
    void setJSONRestrictionsOnAccess(String restrictionsOnAccess);

    @JavaHandler
    void setRestrictionsOnAccess(List<String> restrictionsOnAccess) throws JsonProcessingException;

    @JavaHandler
    List<String> getRestrictionsOnAccess();

    @Property("findingAidNote")
    String getJSONFindingAidNote();

    @Property("findingAidNote")
    void setJSONFindingAidNote(String findingAidNote);

    @JavaHandler
    void setFindingAidNote(List<String> findingAidNote) throws JsonProcessingException;

    @JavaHandler
    List<String> getFindingAidNote();

    @Property("eventNote")
    String getJSONEventNote();

    @Property("eventNote")
    void setJSONEventNote(String eventNote);

    @JavaHandler
    void setEventNote(List<String> eventNote) throws JsonProcessingException;

    @JavaHandler
    List<String> getEventNote();

    @Property("uniformTitle")
    String getUniformTitle();

    @Property("uniformTitle")
    void setUniformTitle(String uniformTitle);

    @Property("alternativeTitle")
    String getAlternativeTitle();

    @Property("alternativeTitle")
    void setAlternativeTitle(String alternativeTitle);

    /**
     * summary of scope of work, description of image
     */
    @Property("summary")
    String getSummary();

    @Property("summary")
    void setSummary(String summary);

    /**
     * Provide the Voyager Id of the collection record bib record belongs to
     * Also known as parent local system number 
     */
    @Property("parentBibId")
    String getParentBibId();
    
    /**
     * Set the Voyager Id of the collection record bib record belongs to
     * Also known as parent local system number
     */
    @Property("parentBibId")
    void setParentBibId(String parentBibId);

    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    String getBibId();

    /**
     * Also known as localsystmno
     */
    @Property("bibId")
    void setBibId(String bibId);

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    String getPublicNotes();

    /**
     * To be published in the catalogue
     */
    @Property("publicNotes")
    void setPublicNotes(String publicNotes);

    @Property("australianContent")
    Boolean isAustralianContent();

    @Property("australianContent")
    void setAustralianContent(Boolean australianContent);

    @Property("materialFromMultipleSources")
    void setMaterialFromMultipleSources(Boolean materialFromMultipleSources);

    @Property("materialFromMultipleSources")
    Boolean getMaterialFromMultipleSources();

    @Property("acquisitionStatus")
    String getAcquisitionStatus();

    @Property("acquisitionStatus")
    void setAcquisitionStatus(String acquisitionStatus);

    @Property("acquisitionCategory")
    String getAcquisitionCategory();

    @Property("acquisitionCategory")
    void setAcquisitionCategory(String acquisitionCategory);

    @Property("additionalTitle")
    void setAdditionalTitle(String additionalTitle);

    @Property("additionalTitle")
    String getAdditionalTitle();

    @Property("additionalContributor")
    void setAdditionalContributor(String additionalContributor);

    @Property("additionalContributor")
    String getAdditionalContributor();

    @Property("additionalCreator")
    void setAdditionalCreator(String additionalCreator);

    @Property("additionalCreator")
    String getAdditionalCreator();

    @Property("additionalSeries")
    void setAdditionalSeries(String additionalSeries);

    @Property("additionalSeries")
    String getAdditionalSeries();

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    Iterable<Work> getDeliveryWorks();

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    void addDeliveryWork(Work deliveryWork);

    @Adjacency(label = DeliveredOn.label, direction = Direction.OUT)
    void removeDeliveryWork(Work deliveryWork);

    @JavaHandler
    List<String> getDeliveryWorkIds();

    @JavaHandler
    void removeDeliveryWorks();

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    void setDeliveryWorkParent(final Work interview);

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    Work getDeliveryWorkParent();

    @Adjacency(label = DeliveredOn.label, direction = Direction.IN)
    void removeDeliveryWorkParent(final Work interview);

    @Incidence(label = DeliveredOn.label, direction = Direction.IN)
    Iterable<DeliveredOn> getDeliveryWorkParentEdges();

    @JavaHandler
    DeliveredOn getDeliveryWorkParentEdge();

    @JavaHandler
    void setDeliveryWorkOrder(int position);

    @Adjacency(label = IsPartOf.label)
    void setParent(final Work parent);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void addChild(final Work part);

    @Adjacency(label = IsPartOf.label)
    Work getParent();

    @Incidence(label = IsPartOf.label, direction = Direction.OUT)
    Iterable<IsPartOf> getParentEdges();

    @JavaHandler
    IsPartOf getParentEdge();

    @JavaHandler
    void setOrder(int position);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Iterable<Work> getChildren();

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', subType.code)")
    Iterable<Work> getLeafs(@GremlinParam("subType") SubType subType);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.loop(3){true}{true}.has('subType', Subtype.fromString(T).in, subTypes)")
    Iterable<Work> getLeafs(@GremlinParam("subTypes") List<String> subTypes);

    @GremlinGroovy("it.inE.has('label', 'isPartOf').outV.has('subType', subType.code)")
    Iterable<Section> getSections(@GremlinParam("subType") SubType subType);

    @JavaHandler
    Section asSection();

    @JavaHandler
    EADWork asEADWork();

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    void addCopy(final Copy copy);

    /**
     * This method is intended for internal amberdb use, to be called by the
     * removeRepresentation() method.  You probably want to use removeRepresentation()
     * method to remove a representative image.
     * @param copy The representative copy
     */
    @Adjacency(label = Represents.label, direction = Direction.IN)
    void removeRepresentative(final Copy copy);

    /**
     * This method calls removeRepresentative() to remove a representative image,
     * and update the hasRepresentation flag which is a shortcut for delivery.
     * @param copy The representative copy
     */
    @JavaHandler
    void removeRepresentation(final Copy copy);

    @JavaHandler
    Map<String, Collection<Copy>> getOrderedCopyMap();

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    void removeCopy(final Copy copy);
    
    @JavaHandler
    void removeCopies(List<CopyRole> copyRoles);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    Iterable<Copy> getCopies();

    @GremlinGroovy("it.in('isCopyOf').order{it.a.id <=> it.b.id}")
    Iterable<Copy> getOrderedCopies();

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code).order{it.a.id <=> it.b.id}")
    Iterable<Copy> getOrderedCopies(@GremlinParam("role") CopyRole role);

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    Iterable<Copy> getCopies(@GremlinParam("role") CopyRole role);

    @GremlinGroovy("it.in('isCopyOf').has('copyRole',role.code)")
    Copy getCopy(@GremlinParam("role") CopyRole role);

    @JavaHandler
    Copy getCopy(CopyRole role, int index);

    /**
     * Get the first not null copy from the specified list of Copy roles
     * @param role
     * @return
     */
    @JavaHandler
    Copy getFirstExistingCopy(CopyRole... roles);

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Section addSection();

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    Page addPage();

    /**
     * This method detatches the page from this work, but the page continues to
     * exist as an orphan. Use the deletePage method in AmberSession to actually
     * delete the page with copies and files from the graph.
     *
     * @param page
     *
     *            Note: remove is a naming convention used by tinkerpop frames
     *            annotation.
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void removePage(final Page page);

    /**
     * This method is intended for internal amberdb use, to be called by the
     * addRepresentation() method.  You probably want to use addRepresentation()
     * method to add a representative image.
     * @param copy The representative copy
     */
    @Adjacency(label = Represents.label, direction = Direction.IN)
    void addRepresentative(final Copy copy);

    /**
     * This method calls addRepresentative() to add a representative image,
     * and update the hasRepresentation flag which is a shortcut for delivery.
     * @param copy The representative copy
     */
    @JavaHandler
    void addRepresentation(final Copy copy);

    @Adjacency(label = IsCopyOf.label, direction = Direction.IN)
    Copy addCopy();

    @Adjacency(label = Represents.label, direction = Direction.IN)
    Iterable<Copy> getRepresentations();

    @JavaHandler
    boolean isRepresented();

    /**
     * Adds a page Work and create a MASTER_COPY Copy Node with a File for it
     */
    @JavaHandler
    Page addPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    Page addLegacyDossPage(Path sourceFile, String mimeType) throws IOException;

    @JavaHandler
    Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType) throws IOException;

    @JavaHandler
    Iterable<Page> getPages();

    @JavaHandler
    int countParts();

    @JavaHandler
    int countCopies();

    /**
     * This method detaches the part from this work, but the part continues to
     * exist as an orphan. Use the deletePart method to actually delete the part
     * and its children from the graph.
     *
     * @param part The part ot be removed
     */
    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    void removePart(final Work part);

    @JavaHandler
    Page getPage(int position);

    @JavaHandler
    Work getLeaf(SubType subType, int position);

    @JavaHandler
    void loadPagedWork() throws InvalidSubtypeException;

    @JavaHandler
    List<Work> getPartsOf(List<String> subTypes);

    @JavaHandler
    List<Work> getExistsOn(List<String> subTypes);

    @JavaHandler
    List<Work> getPartsOf(String subType);

    @JavaHandler
    List<Work> getExistsOn(String subType);

    /**
     * This method sets the edge order of the related Nodes in the list to be
     * their index in the list. The related Nodes and their edge association to
     * this object must already exist.
     *
     * @param relatedNodes
     *            A list of related Nodes whose edges will have their edge
     *            ordering updated.
     * @param label
     *            The label or type of edge to be updated (eg: 'isPartOf',
     *            'existsOn')
     * @param direction
     *            The direction of the edge from this object
     */
    @JavaHandler
    void orderRelated(List<Work> relatedNodes, String label, Direction direction);

    /**
     * Orders the parts in the given list by their list order. This is a
     * specialization of orderRelated.
     *
     * @param parts
     *            The list of parts.
     */
    @JavaHandler
    void orderParts(List<Work> parts);

    @JavaHandler
    List<String> getJsonList(String propertyName);

    @JavaHandler
    boolean hasBornDigitalCopy();

    @JavaHandler
    boolean hasMasterCopy();

    @JavaHandler
    boolean hasCopyRole(CopyRole role);

    /**
     * @return true if work has any of the copy roles in the list
     */
    @JavaHandler
    boolean hasCopyRole(List<CopyRole> copyRoles);

    @JavaHandler
    Copy getOrCreateCopy(CopyRole role);

    @JavaHandler
    boolean hasUniqueAlias(AmberSession session);
    
    @JavaHandler
    Work getRepresentativeImageWork();

    @JavaHandler
    boolean hasImageAccessCopy();

    @Property("coordinates")
    void setJSONCoordinates(String coordinates);

    @Property("coordinates")
    String getJSONCoordinates();

    @JavaHandler
    void addCoordinates(Coordinates coordinates) throws JsonProcessingException;

    @JavaHandler
    void setCoordinates(List<Coordinates> coordinatesList) throws JsonProcessingException;

    @JavaHandler
    List<Coordinates> getCoordinates();

    @JavaHandler
    Coordinates getCoordinates(int index);

    @Property("carrier")
    String getCarrier();

    @Property("carrier")
    void setCarrier(String carrier);



    /**
     * Get the order in which this work appears on it's parent. I.E. The order of a 5th page would be 5. Return null
     * if work has no order (that is, it does not have a parent).
     */
    @JavaHandler
    Integer getOrder();

    @JavaHandler
    boolean isVoyagerRecord();

    @JavaHandler
    void addToCommentsExternal(String comment);




    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, Work {
        static ObjectMapper mapper = new ObjectMapper();

        @Override
        public Acknowledge addAcknowledgement(final Party party, final String ackType, final String kindOfSupport,
                final Double weighting, final Date dateOfAck, final String urlToOriginal) {
            Acknowledge ack = addAcknowledgement(party);
            ack.setAckType(ackType);
            ack.setKindOfSupport(kindOfSupport);
            ack.setWeighting(weighting);
            ack.setUrlToOriginal(urlToOriginal);
            ack.setDate(dateOfAck);
            return ack;
        }

        @Override
        public List<Acknowledge> getOrderedAcknowledgements() {
            List<Acknowledge> list = Lists.newArrayList(getAcknowledgements());

            Collections.sort(list, new Comparator<Acknowledge>() {
                public int compare(final Acknowledge object1, final Acknowledge object2) {
                    return object1.getWeighting().compareTo(object2.getWeighting());
                }
            });

            return list;
        }

        @Override
        public Page addPage(Path sourceFile, String mimeType) throws IOException {
            Page page = addPage();
            page.addCopy(sourceFile, CopyRole.MASTER_COPY, mimeType);
            return page;
        }

        @Override
        public Page addLegacyDossPage(Path dossPath, String mimeType) throws IOException {
            Page page = addPage();
            page.addLegacyDossCopy(dossPath, CopyRole.MASTER_COPY, mimeType);
            return page;
        }

        @Override
        public Copy addCopy(Path sourceFile, CopyRole copyRole, String mimeType) throws IOException {
            Copy copy = addCopy();
            copy.setCopyRole(copyRole.code());
            copy.addFile(sourceFile, mimeType);
            return copy;
        }

        @Override
        public Copy addLegacyDossCopy(Path dossPath, CopyRole copyRole, String mimeType) throws IOException {
            Copy copy = addCopy();
            copy.setCopyRole(copyRole.code());
            copy.addLegacyDossFile(dossPath, mimeType);
            return copy;
        }

        @Override
        public List<Page> getPages() {
            List<Page> pages = new ArrayList<>();
            Iterable<Work> parts = this.getChildren();
            if (parts != null) {
                for (Work part : parts) {
                    pages.add(frame(part.asVertex(), Page.class));
                }
            }
            return pages;
        }

        @Override
        public Page getPage(int position) {
            if (position <= 0)
                throw new IllegalArgumentException("Cannot get this page, invalid input position " + position);

            Iterable<Page> pages = this.getPages();
            if (pages == null || countParts() < position)
                throw new IllegalArgumentException("Cannot get this page, page at position " + position + " does not exist.");

            Iterator<Page> pagesIt = pages.iterator();
            int counter = 1;
            Page page = null;
            while (pagesIt.hasNext()) {
                page = pagesIt.next();
                if (counter == position)
                    return page;
                counter++;
            }
            return page;
        }

        @Override
        public Work getLeaf(SubType subType, int position) {
            if (position <= 0)
                throw new IllegalArgumentException("Cannot get this page, invalid input position " + position);

            Iterable<Work> leafs = getLeafs(subType);
            if (leafs == null)
                throw new IllegalArgumentException("Cannot get this page, page at position " + position + " does not exist.");

            int counter = 1;
            for (Work leaf : leafs) {
                if (counter == position)
                    return leaf;
            }
            return null;
        }

        /**
         * Count the number of copies this work has.
         */
        @Override
        public int countCopies() {
            return Lists.newArrayList(this.getCopies()).size();
        }

        @Override
        public int countParts() {
            return (parts() == null) ? 0 : parts().size();
        }

        @Override
        public Section asSection() {
            return frame(this.asVertex(), Section.class);
        }

        @Override
        public EADWork asEADWork() {
            return frame(this.asVertex(), EADWork.class);
        }

        @Override
        public String getHoldingNumberAndId() {
            return getHoldingNumber() + (getHoldingId() != null ? ("|:|" + getHoldingId()) : "");
        }

        @Override
        public void setHoldingNumberAndId(String holdNumAndId) {
            if (holdNumAndId == null || holdNumAndId.isEmpty()) {
                setHoldingNumber(null);
                setHoldingId(null);
            } else {
                List<String> splitted = Lists.newArrayList(Splitter.on("|:|").split(holdNumAndId));
                if (splitted.size() == 2) {
                    setHoldingNumber(splitted.get(0));
                    setHoldingId(splitted.get(1));
                }
                else if (splitted.size() == 1) {
                    setHoldingNumber(splitted.get(0));
                }
            }
        }

        private List<Edge> parts() {
            return (gremlin().inE(IsPartOf.label) == null) ? null : gremlin().inE(IsPartOf.label).toList();
        }

        private AmberVertex asAmberVertex() {
            if (this.asVertex() instanceof WrappedVertex) {
                return (AmberVertex) ((WrappedVertex) this.asVertex()).getBaseVertex();
            } else {
                return (AmberVertex) this.asVertex();
            }
        }

        /**
         * Loads all of a work into the session including Pages with their
         * Copies and Files but not the work's representative Copy
         */
        public void loadPagedWork() {
            loadPagedWork(false);
        }

        /**
         * Loads all of a work into the session including Pages with their
         * Copies and Files including its representative Copy (if it exists)
         */
        public void loadPagedWork(boolean includeRepresentativeCopies) {
            AmberVertex work = this.asAmberVertex();
            AmberGraph g = work.getAmberGraph();
            AmberQuery query = g.newQuery((Long) work.getId());
            query.branch(new String[] {"isPartOf"}, Direction.BOTH);

            if (includeRepresentativeCopies) {
                query.branch(BRANCH_FROM_ALL, new String[] {"represents"}, Direction.IN);
            }

            query.branch(BRANCH_FROM_ALL, new String[] {"deliveredOn"}, Direction.IN) // gets delivery parent
                 .branch(BRANCH_FROM_ALL, new String[] {"isCopyOf"}, Direction.IN)
                 .branch(BRANCH_FROM_PREVIOUS, new String[] {"isFileOf"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"descriptionOf"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"tags"}, Direction.IN)
                 .branch(BRANCH_FROM_ALL, new String[] {"acknowledge"}, Direction.OUT)
                 .execute(true);
        }

        public List<Work> getPartsOf(List<String> subTypes) {

            AmberVertex work = this.asAmberVertex();

            // just return the pages
            List<Edge> partEdges = Lists.newArrayList(work.getEdges(Direction.IN, "isPartOf"));
            List<Work> works = new ArrayList<>();
            for (Edge e : partEdges) {
                Vertex v = e.getVertex(Direction.OUT);
                if (subTypes == null || subTypes.size() == 0 || subTypes.contains(v.getProperty("subType"))) {
                    works.add(this.g().frame(v, Work.class));
                }
            }
            return works;
        }

        public List<Work> getExistsOn(List<String> subTypes) {

            AmberVertex work = this.asAmberVertex();

            // just return the pages
            List<Edge> partEdges = Lists.newArrayList(work.getEdges(Direction.OUT, "existsOn"));
            List<Work> works = new ArrayList<>();
            for (Edge e : partEdges) {
                Vertex v = e.getVertex(Direction.IN);
                if (subTypes == null || subTypes.size() == 0 || subTypes.contains(v.getProperty("subType"))) {
                    works.add(this.g().frame(v, Work.class));
                }
            }
            return works;
        }

        public List<Work> getPartsOf(String subType) {
            return getPartsOf(Arrays.asList(new String[]{subType}));
        }

        public List<Work> getExistsOn(String subType) {
            return getExistsOn(Arrays.asList(new String[]{subType}));
        }

        @Override
        public void setOrder(int position) {
            IsPartOf parentEdge = getParentEdge();
            if (parentEdge != null) {
                parentEdge.setRelOrder(position);
            }
        }

        @Override
        public IsPartOf getParentEdge() {
            Iterator<IsPartOf> iterator = getParentEdges().iterator();
            return (iterator != null && iterator.hasNext()) ? iterator.next() : null;
        }

        @Override
        public Set<String> getConstraint() {
            List<String> list = deserialiseJSONString(getJSONConstraint());
            LinkedHashSet<String> constraint = new LinkedHashSet<>();
            constraint.addAll(list);
            return constraint;
        }

        @Override
        public void setConstraint(Set<String> constraint) throws JsonProcessingException {
            setJSONConstraint(serialiseToJSON(constraint));
        }

        @Override
        public List<String> getSensitiveReason() {
            return deserialiseJSONString(getJSONSensitiveReason());
        }

        @Override
        public void setSensitiveReason(List<String> sensitiveReason) throws JsonProcessingException {
            setJSONSensitiveReason(serialiseToJSON(sensitiveReason));
        }

        @Override
        public List<String> getRestrictionsOnAccess() {
            return deserialiseJSONString(getJSONRestrictionsOnAccess());
        }

        @Override
        public void setRestrictionsOnAccess(List<String> restrictionsOnAccess) throws JsonProcessingException {
            setJSONRestrictionsOnAccess(serialiseToJSON(restrictionsOnAccess));
        }

        @Override
        public List<String> getFindingAidNote() {
            return deserialiseJSONString(getJSONFindingAidNote());
        }

        @Override
        public void setFindingAidNote(List<String> findingAidNote) throws JsonProcessingException {
            setJSONFindingAidNote(serialiseToJSON(findingAidNote));
        }

        @Override
        public List<String> getEventNote() {
            return deserialiseJSONString(getJSONEventNote());
        }

        @Override
        public void setEventNote(List<String> eventNote) throws JsonProcessingException {
            setJSONEventNote(serialiseToJSON(eventNote));
        }

        @Override
        public List<String> getSeries() {
            return deserialiseJSONString(getJSONSeries());
        }

        @Override
        public void setSeries(List<String> series) throws JsonProcessingException {
            setJSONSeries(serialiseToJSON(series));
        }

        @Override
        public List<String> getClassification() {
            return deserialiseJSONString(getJSONClassification());
        }

        @Override
        public void setClassification(List<String> classification) throws JsonProcessingException {
            setJSONClassification(serialiseToJSON(classification));
        }

        @Override
        public List<String> getContributor() {
            return deserialiseJSONString(getJSONContributor());
        }

        @Override
        public void setContributor(List<String> contributor) throws JsonProcessingException {
            setJSONContributor(serialiseToJSON(contributor));
        }

        @Override
        public List<String> getCoverage() {
            return deserialiseJSONString(getJSONCoverage());
        }

        @Override
        public void setCoverage(List<String> coverage) throws JsonProcessingException {
            setJSONCoverage(serialiseToJSON(coverage));
        }

        @Override
        public List<String> getOccupation() {
            return deserialiseJSONString(getJSONOccupation());
        }

        @Override
        public void setOccupation(List<String> occupation) throws JsonProcessingException {
            setJSONOccupation(serialiseToJSON(occupation));
        }

        @Override
        public List<String> getOtherTitle() {
            return deserialiseJSONString(getJSONOtherTitle());
        }

        @Override
        public void setOtherTitle(List<String> otherTitle) throws JsonProcessingException {
            setJSONOtherTitle(serialiseToJSON(otherTitle));
        }

        @Override
        public List<String> getStandardId() {
            return deserialiseJSONString(getJSONStandardId());
        }

        @Override
        public void setStandardId(List<String> standardId) throws JsonProcessingException {
            setJSONStandardId(serialiseToJSON(standardId));
        }

        @Override
        public List<String> getSubject() {
            return deserialiseJSONString(getJSONSubject());
        }

        @Override
        public void setSubject(List<String> subject) throws JsonProcessingException {
            // ensure each subject entry is unique
            setJSONSubject((null == subject)? null : serialiseToJSON(new HashSet<>(subject)));
        }

        @Override
        public List<String> getScaleEtc() {
            return deserialiseJSONString(getJSONScaleEtc());
        }

        @Override
        public void setScaleEtc(List<String> scaleEtc) throws JsonProcessingException {
            setJSONScaleEtc(serialiseToJSON(scaleEtc));
        }

        protected List<String> deserialiseJSONString(String json) {
            if (json == null || json.isEmpty())
                return new ArrayList<>();
            try {
                return mapper.readValue(json, new TypeReference<List<String>>() {
                });
            }
            catch (IOException e) {
                throw new DataIntegrityException("Could not deserialize property", e);
            }
        }

        protected String serialiseToJSON(Collection<String> list) throws JsonProcessingException {
            if (list == null || list.isEmpty()) return null;
            return mapper.writeValueAsString(list);
        }

        @Override
        public List<String> getJsonList(String propertyName) {
            return deserialiseJSONString((String) this.asVertex().getProperty(propertyName));
        }

        @Override
        public void orderRelated(List<Work> relatedNodes, String label, Direction direction) {
            for (int i = 0; i < relatedNodes.size(); i++) {
                Work node = relatedNodes.get(i);
                node.setOrder(this, label, direction, i+1);
            }
        }

        @Override
        public void orderParts(List<Work> parts) {
            orderRelated(parts, "isPartOf", Direction.OUT);
        }

        @Override
        public GeoCoding getGeoCoding() {
            return (GeoCoding) getDescription("GeoCoding");
        }

        @Override
        public IPTC getIPTC() {
            return (IPTC) getDescription("IPTC");
        }

        @Override
        public List<String> getDcmAltPi() {
            return deserialiseJSONString(getJSONDcmAltPi());
        }

        @Override
        public void setDcmAltPi(List<String> list) throws JsonProcessingException {
            setJSONDcmAltPi(serialiseToJSON(list));
        }

        @Override
        public boolean isRepresented() {
            Iterable<Copy> representations = getRepresentations();
            return representations != null && Iterables.size(representations) != 0;
        }

        @Override
        public void removeRepresentation(final Copy copy) {
            removeRepresentative(copy);
        }

        @Override
        public void addRepresentation(final Copy copy) {
            addRepresentative(copy);
        }

        @Override
        public List<String> getDeliveryWorkIds() {
            Iterable<Work> deliveryWorks = getDeliveryWorks();

            List<String> ids = new ArrayList<>();
            for (Work work : deliveryWorks) {
                ids.add(work.getObjId());
            }

            return ids;
        }

        @Override
        public Work getRepresentativeImageWork() {
            return (Work)this.asVertex().getProperty("representativeImageWork");
        }

        @Override
        public void removeDeliveryWorks() {
            Iterable<Work> deliveryWorks = getDeliveryWorks();
            for (Work dw : deliveryWorks) {
                dw.removeDeliveryWorkParent(this);
            }
        }

        @Override
        public void setDeliveryWorkOrder(int position) {
            getDeliveryWorkParentEdge().setRelOrder(position);
        }

        @Override
        public DeliveredOn getDeliveryWorkParentEdge() {
            Iterator<DeliveredOn> iterator = getDeliveryWorkParentEdges().iterator();
            return (iterator != null && iterator.hasNext()) ? iterator.next() : null;
        }

        @Override
        public boolean hasBornDigitalCopy() {
            Copy origCopy = getCopy(CopyRole.ORIGINAL_COPY);
            return (origCopy != null) && CopyType.BORN_DIGITAL.code().equals(origCopy.getCopyType());
        }

        @Override
        public boolean hasMasterCopy() {
            return hasCopyRole(CopyRole.MASTER_COPY);
        }

        @Override
        public boolean hasCopyRole(CopyRole role) {
            return getCopy(role) != null;
        }

        @Override
        public boolean hasCopyRole(List<CopyRole> copyRoles){
            if (copyRoles != null){
                for (CopyRole copyRole: copyRoles){
                    if (hasCopyRole(copyRole)){
                        return true;
                    }
                }
            }
            return false;
        }

        @Override
        public Copy getOrCreateCopy(CopyRole role) {
            Copy copy = getCopy(role);
            if (copy == null) {
                copy = addCopy();
                copy.setCopyRole(role.code());
            }
            return copy;
        }

        @Override
        public Copy getFirstExistingCopy(CopyRole... roles){
            if (roles != null){
                for (CopyRole copyRole : roles){
                    Copy copy = getCopy(copyRole);
                    if (copy != null){
                        return copy;
                    }
                }
            }
            return null;
        }

        @Override
        public boolean hasUniqueAlias(AmberSession session) {
            List<String> aliases = getAlias();
            if (aliases == null || aliases.size() == 0 || aliases.size() > 1) {
                return false;
            } else {
                String alias = aliases.get(0);
                List<Work> works = session.findModelByValueInJsonList("alias", alias, Work.class);
                if (works.size() > 1) {
                    // Has more than 1 work with the same alias
                    return false;
                }
            }

            return true;
        }

        @Override
        public Map<String, Collection<Copy>> getOrderedCopyMap() {
            LinkedListMultimap<String, Copy> orderedCopyMap = LinkedListMultimap.create();
            for (Copy copy : getOrderedCopies()) {
                orderedCopyMap.put(copy.getCopyRole(), copy);
            }
            return orderedCopyMap.asMap();
        }

        @Override
        public boolean hasImageAccessCopy(){
            Copy accessCopy = getCopy(CopyRole.ACCESS_COPY);
            accessCopy = accessCopy == null ? getCopy(CopyRole.ACCESS_ONLY_COPY) : null;
            return accessCopy != null && accessCopy.getImageFile() != null;
        }

        @Override
        public Copy getCopy(CopyRole role, int index) {
            List<Copy> orderedCopies = Lists.newArrayList(getOrderedCopies(role));
            if (orderedCopies == null) {
                return null;
            }

            if (orderedCopies.size() -1 < index) {
                return null;
            }

            return orderedCopies.get(index);
        }

        @Override
        public Coordinates getCoordinates(int index) {
            List<Coordinates> allCoordinates = getCoordinates();
            return allCoordinates.get(index);
        }

        @Override
        public void addCoordinates(Coordinates coordinates) throws JsonProcessingException {
            List<Coordinates> allCoordinates = getCoordinates();
            allCoordinates.add(coordinates);
            setCoordinates(allCoordinates);
        }

        @Override
        public void setCoordinates(List<Coordinates> coordinatesList) throws JsonProcessingException {
            setJSONCoordinates(mapper.writeValueAsString(coordinatesList));
        }

        @Override
        public List<Coordinates> getCoordinates() {
            String json = getJSONCoordinates();
            if (json == null || json.isEmpty()) {
                return new ArrayList<>();
            }
            return deserialiseJSONString(json, new TypeReference<List<Coordinates>>() {});
        }

        @Override
        public Integer getOrder() {
            final Work currWork = this;
            final long currId = currWork.getId();
            Work parent = getParent();
            if (parent == null) {
                return null;
            }
            Iterable<Page> pages = parent.getPages();
            int order = Iterables.indexOf(pages, new Predicate<Work>() {
                @Override
                public boolean apply(Work work) {
                    return currId == work.getId();
                }
            });
            return order+1;
        }

        @Override
        public boolean isVoyagerRecord() {
            return StringUtils.isNotBlank(getBibId()) && StringUtils.equalsIgnoreCase(getRecordSource(), "voyager");
        }
        
        @Override
        public void removeCopies(List<CopyRole> copyRoles) {
            for (CopyRole copyRole : copyRoles){
                Iterator<Copy> copies = getCopies(copyRole).iterator();
                while (copies.hasNext()) {
                    removeCopy(copies.next());
                }
            }
        }

        /**
         * Appends the provided text to the External Comments fields of the work, only if the text does not already exist in the field (case-insensitive).
         *
         * If the work already has an external comment, the existing text will be terminated with a full stop (if not already present), then the new comment will be appended.
         *
         * @param comment the new text to add to the comments field
         */
        @Override
        public void addToCommentsExternal(String comment) {
            boolean hasExternalComment = isNotBlank(getCommentsExternal());
            if (!hasExternalComment || !containsIgnoreCase(getCommentsExternal(), comment)) {
                String existing = trimToEmpty(getCommentsExternal());
                if (existing.matches(".*[\\.;:]$")) {
                    existing = existing.substring(0, existing.length() - 1);
                }

                setCommentsExternal(((hasExternalComment ? existing + ". " : "") + comment).trim());
            }
        }
    }
}
