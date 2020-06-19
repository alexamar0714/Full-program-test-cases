package amberdb.model;

import amberdb.NoSuchObjectException;
import amberdb.relation.IsPartOf;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@TypeValue("EADWork")
public interface EADWork extends Work {
    @Property("rdsAcknowledgementType")
    public String getRdsAcknowledgementType();

    @Property("rdsAcknowledgementType")
    public void setRdsAcknowledgementType(String rdsAcknowledgementType);

    @Property("rdsAcknowledgementReceiver")
    public String getRdsAcknowledgementReceiver();

    @Property("rdsAcknowledgementReceiver")
    public void setRdsAcknowledgementReceiver(String rdsAcknowledgementReceiver);

    @Property("eadUpdateReviewRequired")
    public String getEADUpdateReviewRequired();

    @Property("eadUpdateReviewRequired")
    public void setEADUpdateReviewRequired(String eadUpdateReviewRequired);

    /**
     * scopeContent: scope of content (aka: abstract) for this EAD work.
     */
    @Property("scopeContent")
    public String getScopeContent();

    /**
     * scopeContent: scope of content (aka: abstract) for this EAD work.
     */
    @Property("scopeContent")
    public void setScopeContent(String scopeContent);

    /**
     * bibliography: bibliography info for a person.
     */
    @Property("bibliography")
    public String getJSONBibliography();

    /**
     * bibliography: bibliography info for a person.
     */
    @Property("bibliography")
    public void setJSONBibliography(String bibliography);

    @JavaHandler
    public List<String> getBibliography() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setBibliography(List<String> bibliography) throws JsonParseException, JsonMappingException, IOException;

    /**
     * arrangement: the arrangement for the collection in hierarchical components
     */
    @Property("arrangement")
    public String getJSONArrangement();

    /**
     * arrangement: the arrangement for the collection in hierarchical components
     */
    @Property("arrangement")
    public void setJSONArrangement(String arrangement);

    @JavaHandler
    public List<String> getArrangement() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setArrangement(List<String> arrangement) throws JsonParseException, JsonMappingException, IOException;


    @Property("access")
    public String getJSONAccess();

    @Property("access")
    public void setJSONAccess(String access);

    @JavaHandler
    public List<String> getAccess() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setAccess(List<String> access) throws JsonParseException, JsonMappingException, IOException;


    @Property("copyingPublishing")
    public String getJSONCopyingPublishing();

    @Property("copyingPublishing")
    public void setJSONCopyingPublishing(String copyingPublishing);

    @JavaHandler
    public List<String> getCopyingPublishing() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setCopyingPublishing(List<String> copyingPublishing) throws JsonParseException, JsonMappingException, IOException;


    @Property("preferredCitation")
    public String getJSONPreferredCitation();

    @Property("preferredCitation")
    public void setJSONPreferredCitation(String preferredCitation);

    @JavaHandler
    public List<String> getPreferredCitation() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setPreferredCitation(List<String> preferredCitation) throws JsonParseException, JsonMappingException, IOException;


    @Property("relatedMaterial")
    public String getJSONRelatedMaterial();

    @Property("relatedMaterial")
    public void setJSONRelatedMaterial(String relatedMaterial);

    @JavaHandler
    public List<String> getRelatedMaterial() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setRelatedMaterial(List<String> relatedMaterial) throws JsonParseException, JsonMappingException, IOException;

    /**
     * adminInfo: administrative info for an origanisation.
     */
    @Property("adminInfo")
    public String getAdminInfo();

    /**
     * adminInfo: administrative info for an origanisation.
     */
    @Property("adminInfo")
    public void setAdminInfo(String adminInfo);


    /**
     * correspondenceIndex: provide summary of correspondence indexed to entities associated
     * with this EADWork.
     */
    @Property("correspondenceIndex")
    public String getCorrespondenceIndex();

    /**
     * correspondenceIndex: provide summary of correspondence indexed to entities associated
     * with this EADWork.
     */
    @Property("correspondenceIndex")
    public void setCorrespondenceIndex(String correspondenceIndex);

    @Property("provenance")
    public String getJSONProvenance();

    @Property("provenance")
    public void setJSONProvenance(String provenance);

    @JavaHandler
    public List<String> getProvenance() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setProvenance(List<String> provenance) throws JsonParseException, JsonMappingException, IOException;

    @Property("altform")
    public String getAltForm();

    @Property("altform")
    public void setAltForm(String altform);

    @Property("dateRangeInAS")
    public String getDateRangInAS();

    @Property("dateRangeInAS")
    public void setDateRangeInAS(String dateRangeInAS);

    @JavaHandler
    public String getFmttedDateRange() throws JsonParseException, JsonMappingException, IOException;

    /**
     * repository: the repository that holds the collection this EADWork belongs to.
     */
    @Property("repository")
    public String getRepository();

    /**
     * repository: the repository that holds the collection this EADWork belongs to.
     */
    @Property("repository")
    public void setRepository(String repository);

    @Property("collectionNumber")
    public String getCollectionNumber();

    @Property("collectionNumber")
    public void setCollectionNumber(String collectionNumber);

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getFolder() to get this property.
     */
    @Property("folder")
    public String getJSONFolder();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setFolder() to set this property.
     */
    @Property("folder")
    public void setJSONFolder(String folder);

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getFolderType() to get this property.
     */
    @Property("folderType")
    public String getJSONFolderType();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setFolderType() to set this property.
     */
    @Property("folderType")
    public void setJSONFolderType(String folderType);

    @JavaHandler
    public List<String> getFolderType() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setFolderType(List<String> folderTypes) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * getFolderNumber() to get this property.
     */
    @Property("folderNumber")
    public String getJSONFolderNumber();

    /**
     * This property is encoded as a JSON Array - You probably want to use
     * setFolderNumber() to set this property.
     */
    @Property("folderNumber")
    public void setJSONFolderNumber(String folderNumber);

    @JavaHandler
    public List<String> getFolderNumber() throws JsonParseException, JsonMappingException, IOException;

    @JavaHandler
    public void setFolderNumber(List<String> folderNumber) throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON deserialisation of the folder property.
     * Each folder entry is returned as:
     *    container <folder type> <folder number>(id:<folder uuid>):<folder label>:(parent:<parent folder uuid>)
     * the (parent:<parent folder uuid>) is optional, and it isn't present if there's no parent folder.
     */
    @JavaHandler
    public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException;

    /**
     * This method handles the JSON serialisation of the folder property.
     * Each folder input entry should be formatted as <folder type>-<folder number>
     */
    @JavaHandler
    public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException;

    @Adjacency(label = IsPartOf.label, direction = Direction.IN)
    public EADWork addEADWork();

    @JavaHandler
    public EADWork getEADWork(long objectId);

    @JavaHandler
    public EADWork checkEADWorkInCollectionByLocalSystemNumber(String localSystemNumber);

    @Property("correspondenceHeader")
    public String getCorrespondenceHeader();

    @Property("correspondenceHeader")
    public void setCorrespondenceHeader(String correspondenceHeader);

    @Property("correspondenceId")
    public String getCorrespondenceId();

    @Property("correspondenceId")
    public void setCorrespondenceId(String correspondenceId);

    abstract class Impl extends Work.Impl implements JavaHandlerContext<Vertex>, EADWork {
        SimpleDateFormat dateFmt = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");

        @Override
        public String getFmttedDateRange() throws JsonParseException, JsonMappingException, IOException {
            Date from = getStartDate();
            Date to = getEndDate();

            SimpleDateFormat yearFmt = new SimpleDateFormat("yyyy");
            String fmttedFrom = (from == null)?"":dateFmt.format(from);
            String fmttedTo = (to == null)? "" : dateFmt.format(to);

            if (fmttedFrom.startsWith("01/01") && (fmttedFrom.endsWith("00:00:00") || fmttedFrom.endsWith("12:00:00"))) {
                fmttedFrom = yearFmt.format(from);
            }

            if (fmttedTo.startsWith("31/12") && (fmttedTo.endsWith("23:59:59") || fmttedTo.endsWith("12:00:00"))) {
                fmttedTo = yearFmt.format(to);
            }
            return fmttedFrom + " - " + fmttedTo;
        }

        @Override
        public List<String> getFolder() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolder());
        }

        @Override
        public void setFolder(List<String> folder) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolder(serialiseToJSON(folder));
        }

        @Override
        public List<String> getFolderType() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolderType());
        }

        @Override
        public void setFolderType(List<String> folderTypes) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolderType(serialiseToJSON(folderTypes));
        }

        @Override
        public List<String> getFolderNumber() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONFolderNumber());
        }

        @Override
        public void setFolderNumber(List<String> folderNumbers) throws JsonParseException, JsonMappingException, IOException {
            setJSONFolderNumber(serialiseToJSON(folderNumbers));
        }

        @Override
        public List<String> getArrangement() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONArrangement());
        }

        @Override
        public void setArrangement(List<String> arrangement) throws JsonParseException, JsonMappingException, IOException {
            setJSONArrangement(serialiseToJSON(arrangement));
        }

        @Override
        public List<String> getAccess() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONAccess());
        }

        @Override
        public void setAccess(List<String> access) throws JsonParseException, JsonMappingException, IOException {
            setJSONAccess(serialiseToJSON(access));
        }

        @Override
        public List<String> getCopyingPublishing() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONCopyingPublishing());
        }

        @Override
        public void setCopyingPublishing(List<String> copyingPublishing) throws JsonParseException, JsonMappingException, IOException {
            setJSONCopyingPublishing(serialiseToJSON(copyingPublishing));
        }

        @Override
        public List<String> getPreferredCitation() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONPreferredCitation());
        }

        @Override
        public void setPreferredCitation(List<String> preferredCitation) throws JsonParseException, JsonMappingException, IOException {
            setJSONPreferredCitation(serialiseToJSON(preferredCitation));
        }

        @Override
        public List<String> getRelatedMaterial() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONRelatedMaterial());
        }

        @Override
        public void setRelatedMaterial(List<String> relatedMaterial) throws JsonParseException, JsonMappingException, IOException {
            setJSONRelatedMaterial(serialiseToJSON(relatedMaterial));
        }

        @Override
        public List<String> getProvenance() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONProvenance());
        }

        @Override
        public void setProvenance(List<String> provenance) throws JsonParseException, JsonMappingException, IOException {
            setJSONProvenance(serialiseToJSON(provenance));
        }

        @Override
        public List<String> getBibliography() throws JsonParseException, JsonMappingException, IOException {
            return deserialiseJSONString(getJSONBibliography());
        }

        @Override
        public void setBibliography(List<String> bibliography)  throws JsonParseException, JsonMappingException, IOException {
            setJSONBibliography(serialiseToJSON(bibliography));
        }

        @Override
        public EADWork getEADWork(long objectId) {
            EADWork component = this.g().getVertex(objectId, EADWork.class);
            if (component == null) {
                throw new NoSuchObjectException(objectId);
            }
            return component;
        }

        @Override
        public EADWork checkEADWorkInCollectionByLocalSystemNumber(String localSystemNumber) {
            Iterator<Vertex> worksInCollection = this.g().getVertices("localSystemNumber", localSystemNumber).iterator();
            if (worksInCollection.hasNext()) {
                EADWork eadWork = this.g().getVertex(worksInCollection.next().getId(), EADWork.class);
                if (eadWork.getParent() == this)
                    return eadWork;
            }
            return null;
        }
    }
}

