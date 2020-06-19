package amberdb.model;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Adjacency;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import amber.checksum.Checksum;
import amber.checksum.InvalidChecksumException;
import amberdb.AmberSession;
import amberdb.enums.MaterialType;
import amberdb.relation.DescriptionOf;
import amberdb.relation.IsFileOf;
import doss.Blob;
import doss.BlobStore;
import doss.BlobTx;
import doss.NoSuchBlobException;
import doss.Writable;
import doss.core.Writables;
import doss.local.LocalBlobStore;

@TypeValue("File")
public interface File extends Node {

    @JavaHandler
    public void setBlobIdAndSize(Long id);

    @Property("blobId")
    public Long getBlobId();

    @Property("mimeType")
    public void setMimeType(String mimeType);

    @Property("mimeType")
    public String getMimeType();

    /**
     * Name of the original file upload
     */
    @Property("fileName")
    public void setFileName(String fileName);

    @Property("fileName")
    public String getFileName();

    @Property("fileFormat")
    public void setFileFormat(String fileFormat);

    @Property("fileFormat")
    public String getFileFormat();

    @Property("fileFormatVersion")
    public void setFileFormatVersion(String fileFormatVersion);

    @Property("fileFormatVersion")
    public String getFileFormatVersion();

    @Property("fileSize")
    public void setFileSize(long fileSize);

    @JavaHandler
    public long getFileSize();

    @Property("compression")
    public void setCompression(String compression);

    @Property("compression")
    public String getCompression();

    @Property("checksum")
    public void setChecksum(String checksum);

    @Property("checksum")
    public String getChecksum();

    @Property("checksumType")
    public void setChecksumType(String checksumType);

    @Property("checksumType")
    public String getChecksumType();

    /**
     * Set the date/time that the checksum was generated.
     */
    @Property("checksumGenerationDate")
    public void setChecksumGenerationDate(Date date);

    /**
     * Get the date/time the checksum was generated.
     */
    @Property("checksumGenerationDate")
    public Date getChecksumGenerationDate();

    @Property("device")
    public String getDevice();

    @Property("device")
    public void setDevice(String device);

    @Property("deviceSerialNumber")
    public String getDeviceSerialNumber();

    @Property("deviceSerialNumber")
    public void setDeviceSerialNumber(String deviceSerialNumber);

    @Property("software")
    public String getSoftware();

    @Property("software")
    public void setSoftware(String software);

    @Property("softwareSerialNumber")
    public String getSoftwareSerialNumber();

    @Property("softwareSerialNumber")
    public void setSoftwareSerialNumber(String softwareSerialNumber);

    @Property("encoding")
    public String getEncoding();

    @Property("encoding")
    public void setEncoding(String encoding);

    /**
     * This property is encoded as a JSON Array - You probably want to use getToolId to get this property
     */
    @Property("toolId")
    public String getJSONToolId();

    /**
     * This property is encoded as a JSON Array - You probably want to use setToolId to set this property
     */
    @Property("toolId")
    public void setJSONToolId(String toolId);
    
    @Property("type")
    public String getType();
    
    @Property("type")
    public void setType(String type);

    /**
     * This method handles the JSON deserialisation of the toolId Property
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public List<Long> getToolId();

    /**
     * This method handles the JSON serialisation of the toolId Property
     * @throws IOException
     * @throws JsonMappingException
     * @throws JsonParseException
     */
    @JavaHandler
    public void setToolId(List<Long> toolId) throws JsonProcessingException;

    /*
     * Fields migrated from DCM
     */
    @Property("dcmCopyPid")
    public String getDcmCopyPid();

    @Property("dcmCopyPid")
    public void setDcmCopyPid(String dcmCopyPid);

    @Adjacency(label = IsFileOf.label)
    public Copy getCopy();

    @JavaHandler
    public long getSize();

    @JavaHandler
    public SeekableByteChannel openChannel() throws IOException;

    @JavaHandler
    public InputStream openStream() throws IOException;

    @JavaHandler
    void put(Path source) throws IOException;
    
    @JavaHandler
    void putWithChecksumValidation(Path source, Checksum checksum) throws IOException;
    
    @JavaHandler
    void putWithChecksumValidation(Writable source, Checksum checksum) throws IOException;

    @JavaHandler
    void put(Writable writable) throws IOException;
    
    /**
     * This method resets all the technical metadata property values to null, it can
     * be called when a new file is uploaded for a existing copy and the file has a
     * different material type.  
     * 
     * e.g. if a existing copy has a image file, and a new text file is uploaded as the 
     * current version of file for this copy, then:
     *  the technical metadata for the current version of the file would be reset to blank.
     */
    @JavaHandler
    File resetTechnicalProperties(MaterialType materialType);

    @JavaHandler
    void putLegacyDoss(Path dossPath) throws IOException;

    @Adjacency(label = DescriptionOf.label, direction = Direction.IN)
    void removeDescription(final Description description);

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, File {
        static ObjectMapper mapper = new ObjectMapper();
        static Set<String> mandatoryfldNames = new HashSet<>();
        static {
            Method[] methods = File.class.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    mandatoryfldNames.add(method.getName().substring(3).toUpperCase());
                }
            }
        }

        private BlobStore getBlobStore() {
            return AmberSession.ownerOf(g()).getBlobStore();
        }

        private Blob getBlob() throws NoSuchBlobException, IOException {
            if (getBlobId() == null) {
                throw new NoSuchBlobException();
            }
            return getBlobStore().get(getBlobId());
        }

        @Override
        public void setBlobIdAndSize(Long id) {
            try {
                Long size = getBlobStore().get(id).size();
                setFileSize(size);

            } catch (NoSuchBlobException e) {
                setFileSize(0L);

            } catch (IOException e) {
                // clear the fileSize so it can be populated when getFileSize is called
                asVertex().setProperty("fileSize", null);
            }
            asVertex().setProperty("blobId", id);
        }


        /**
         * Tries to return the fileSize attribute of the file vertex first. If this fails,
         * then goes to the blob to get the file's size. If getting the size from the blob
         * is successful (or if this file vertex has no blob).
         *
         * This method's side-effect is to set the fileSize attribute to that reported by
         * the blob, or sets fileSize to 0 if this file has no blob.
         *
         * If an IOException occurs, this method returns 0 without a side effect.
         *
         * @return the file's size in bytes.
         */
        @Override
        public long getFileSize() {
            Long fileSize = this.asVertex().getProperty("fileSize");
            if (fileSize == null) {
                try {
                    fileSize = getBlob().size();
                    setFileSize(fileSize);

                } catch (NoSuchBlobException e) {
                    fileSize = 0L;
                    setFileSize(fileSize);

                } catch (IOException e) {
                    return 0L;
                    // leave fileSize attribute null, so it can try again next time
                }
            }
            return fileSize;
        }

        /**
         * Return the size of the blob. If an IOException occurs try and
         * return the fileSize property. If that fails, return 0L.
         */
        @Override
        public long getSize() {
            try {
                return getBlob().size();

            } catch (NoSuchBlobException e) {
                return 0L; // no blob, so size is 0

            } catch (IOException e) {
                // As a last resort see if the file has a size property to return
                Long fileSize = this.asVertex().getProperty("fileSize");
                if (fileSize != null) {
                    return fileSize;
                }
                // no, so call it 0 for now
                return 0L;
            }
        }

        @Override
        public SeekableByteChannel openChannel() throws IOException {
            return getBlob().openChannel();
        }

        @Override
        public InputStream openStream() throws IOException {
            return getBlob().openStream();
        }

        @Override
        public void put(Path source) throws IOException {
            put(Writables.wrap(source));
        }
        
        @Override
        public void putWithChecksumValidation(Path source, Checksum checksum) throws IOException {
            putWithChecksumValidation(Writables.wrap(source), checksum);
        }

        @Override
        public void put(Writable writable) throws IOException {
            try (BlobTx tx = getBlobStore().begin()) {
                Blob blob = tx.put(writable);
                setBlobIdAndSize(blob.id());
                tx.commit();
            }
        }
        
        @Override
        public void putWithChecksumValidation(Writable writable, Checksum checksum) throws IOException {
            try (BlobTx tx = getBlobStore().begin()) {
                Blob blob = tx.put(writable);
                try {
                    if (!blob.digest(checksum.getAlgorithm().algorithm()).equals(checksum.getValue())){
                        throw new InvalidChecksumException("Ingest failed. Digest mismatch");
                    }
                } catch (NoSuchAlgorithmException e) {
                    throw new InvalidChecksumException("Ingest failed. Checksum not found in the meta xml file");
                }
                setBlobIdAndSize(blob.id());
                tx.commit();
            }
        }

        @Override
        public void putLegacyDoss(Path dossPath) throws IOException {
            try (LocalBlobStore.Tx tx = (LocalBlobStore.Tx) ((LocalBlobStore) getBlobStore()).begin()) {
                Long blobId = tx.putLegacy(dossPath);
                setBlobIdAndSize(blobId);
                tx.commit();
            }
        }
        
        @Override
        public File resetTechnicalProperties(MaterialType materialType) {
            Set<String> properties = this.asVertex().getPropertyKeys();
            for (String property : properties) {
                if (!mandatoryfldNames.contains(property.toUpperCase())) {
                    this.asVertex().removeProperty(property);
                }
            }
            
            File file;
            if (materialType == MaterialType.IMAGE) {
                file = frame(this.asVertex(), ImageFile.class);
                file.setType("ImageFile");
            } else if (materialType == MaterialType.MOVINGIMAGE) {
                file = frame(this.asVertex(), MovingImageFile.class);
                file.setType("MovingImageFile");
            } else if (materialType == MaterialType.SOUND) {
                file = frame(this.asVertex(), SoundFile.class);
                file.setType("SoundFile");
            } else {
                file = frame(this.asVertex(), File.class);
                file.setType("File");
            }
            return file;
        }

        @Override
        public List<Long> getToolId() {
            String toolId = getJSONToolId();
            if (toolId == null || toolId.isEmpty())
                return new ArrayList<Long>();
            return deserialiseJSONString(toolId, new TypeReference<List<Long>>() {});
        }

        @Override
        public void setToolId(List<Long> toolId) throws JsonProcessingException {
            setJSONToolId(mapper.writeValueAsString(toolId));
        }
    }
}
