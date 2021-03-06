//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dataStorage.database;

import gov.nih.nci.caarray.application.fileaccess.TemporaryFileCache;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dataStorage.DataStoreException;
import gov.nih.nci.caarray.dataStorage.StorageMetadata;
import gov.nih.nci.caarray.dataStorage.UnsupportedSchemeException;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.BlobHolder;
import gov.nih.nci.caarray.domain.MultiPartBlob;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * An implementation of data storage based on storing in database as multipart blobs. Appropriate for mysql and other
 * databases which cannot stream data from blobs.
 * 
 * @author dkokotov
 */
public class DatabaseMultipartBlobDataStorage implements DataStorage {
    private static final Logger LOG = Logger.getLogger(DatabaseMultipartBlobDataStorage.class);

    static final String SCHEME = "db-multipart";

    /**
     * Constant holding the default blob size. By default it will be 50 MB.
     */
    private static final int DEFAULT_BLOB_SIZE = 50 * 1024 * 1024;

    private final MultipartBlobDao blobDao;
    private final SearchDao searchDao;
    private final int blobPartSize = DEFAULT_BLOB_SIZE;
    private final Provider<TemporaryFileCache> tempFileCacheSource;

    /**
     * Constructor.
     * 
     * @param blobDao MultipartBlobDao dependency
     * @param searchDao SearchDao dependency
     * @param tempFileCacheSource Provider which will generate TemporaryfileCache instances as needed, used for caching
     *            blob data to disk.
     */
    @Inject
    public DatabaseMultipartBlobDataStorage(MultipartBlobDao blobDao, SearchDao searchDao,
            @Named("dbMultipartStorageTempCache") Provider<TemporaryFileCache> tempFileCacheSource) {
        this.blobDao = blobDao;
        this.searchDao = searchDao;
        this.tempFileCacheSource = tempFileCacheSource;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public StorageMetadata add(InputStream stream, boolean compressed) throws DataStoreException {
        try {
            final MultiPartBlob multiPartBlob = new MultiPartBlob();
            multiPartBlob.setCreationTimestamp(new Date());
            multiPartBlob.writeData(stream, !compressed, this.blobPartSize);
            this.blobDao.save(multiPartBlob);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(makeHandle(multiPartBlob.getId()));
            metadata.setCompressedSize(multiPartBlob.getCompressedSize());
            metadata.setUncompressedSize(multiPartBlob.getUncompressedSize());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata addChunk(URI handle, InputStream stream) throws DataStoreException {
        try {
            MultiPartBlob multiPartBlob;
            if (handle == null) {
                multiPartBlob = new MultiPartBlob();
                multiPartBlob.setCreationTimestamp(new Date());
            } else {
                multiPartBlob = searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
            }
            multiPartBlob.writeData(stream, false, blobPartSize);
            blobDao.save(multiPartBlob);

            final StorageMetadata metadata = new StorageMetadata();
            metadata.setHandle(makeHandle(multiPartBlob.getId()));
            metadata.setPartialSize(multiPartBlob.getUncompressedSize());
            return metadata;
        } catch (final IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public StorageMetadata finalizeChunkedFile(URI handle) {
        MultiPartBlob multiPartBlob =
                searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
        try {
            InputStream is = multiPartBlob.readCompressedContents();
            return add(is, false);
        } catch (IOException e) {
            throw new DataStoreException("Could not add data", e);
        }
    }

    private void checkScheme(URI handle) {
        if (!SCHEME.equals(handle.getScheme())) {
            throw new UnsupportedSchemeException("Unsupported scheme: " + handle.getScheme());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(Collection<URI> handles) throws DataStoreException {
        final List<Long> blobIds = new ArrayList<Long>();
        for (final URI handle : handles) {
            checkScheme(handle);
            blobIds.add(Long.valueOf(handle.getSchemeSpecificPart()));
        }
        this.blobDao.deleteByIds(blobIds);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void remove(URI handle) throws DataStoreException {
        remove(Collections.singleton(handle));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseFile(URI handle, boolean compressed) {
        checkScheme(handle);
        final String tempFileName = fileName(handle.getSchemeSpecificPart(), compressed);
        this.tempFileCacheSource.get().delete(tempFileName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public File openFile(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        final String tempFileName = fileName(handle.getSchemeSpecificPart(), compressed);
        final TemporaryFileCache tempFileCache = this.tempFileCacheSource.get();
        File tempFile = tempFileCache.getFile(tempFileName);
        if (tempFile != null) {
            return tempFile;
        } else {
            tempFile = tempFileCache.createFile(tempFileName);
            final MultiPartBlob blob =
                    this.searchDao.retrieve(MultiPartBlob.class, Long.valueOf(handle.getSchemeSpecificPart()));
            if (blob == null) {
                throw new DataStoreException("No data found for handle " + handle);
            }
            try {
                final OutputStream os = FileUtils.openOutputStream(tempFile);
                copyContentsToStream(blob, !compressed, os);
                IOUtils.closeQuietly(os);
            } catch (final IOException e) {
                throw new DataStoreException("Could not write out file ", e);
            }
            return tempFile;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputStream openInputStream(URI handle, boolean compressed) throws DataStoreException {
        checkScheme(handle);
        try {
            return new AutoCloseInputStream(FileUtils.openInputStream(openFile(handle, compressed)));
        } catch (final IOException e) {
            throw new DataStoreException("Could not open input stream for data: ", e);
        }
    }

    private void copyContentsToStream(MultiPartBlob blob, boolean inflate, OutputStream dest) throws IOException {
        if (refreshIfCleared(blob)) {
            LOG.info("reloaded blobs for " + blob.getId());
        }
        final InputStream in = inflate ? blob.readUncompressedContents() : blob.readCompressedContents();
        try {
            IOUtils.copy(in, dest);
        } finally {
            IOUtils.closeQuietly(in);
            clearAndEvictBlobs(blob);
        }
    }

    private String compressedFileName(String fileHandle) {
        return fileHandle + ".comp";
    }

    private String uncompressedFileName(String fileHandle) {
        return fileHandle + ".unc";
    }

    private String fileName(String fileHandle, boolean compressed) {
        return compressed ? compressedFileName(fileHandle) : uncompressedFileName(fileHandle);
    }

    private boolean refreshIfCleared(MultiPartBlob blobs) {
        final List<BlobHolder> list = blobs.getBlobParts();
        boolean reloaded = false;
        for (final BlobHolder bh : list) {
            if (bh.getContents() == null) {
                this.searchDao.refresh(bh);
                reloaded = true;
            }
        }
        return reloaded;
    }

    private void clearAndEvictBlobs(MultiPartBlob blobs) {
        final List<BlobHolder> parts = blobs.getBlobParts();
        if (parts != null) {
            for (final BlobHolder bh : parts) {
                this.blobDao.evictObject(bh);
                bh.setContents(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterable<StorageMetadata> list() {
        final Set<StorageMetadata> metadatas = Sets.newHashSet();
        final List<MultiPartBlob> blobs = this.searchDao.retrieveAll(MultiPartBlob.class);
        for (final MultiPartBlob blob : blobs) {
            final StorageMetadata sm = new StorageMetadata();
            sm.setCreationTimestamp(blob.getCreationTimestamp());
            sm.setHandle(makeHandle(blob.getId()));
            sm.setCompressedSize(blob.getCompressedSize());
            sm.setUncompressedSize(blob.getUncompressedSize());
            metadatas.add(sm);
        }
        return metadatas;
    }

    private URI makeHandle(Long id) {
        return CaArrayUtils.makeUriQuietly(SCHEME, String.valueOf(id));
    }
}
