/*
 * Copyright 2017 Axway Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.axway.ats.action.s3;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.Protocol;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.axway.ats.common.PublicAtsApi;
import com.axway.ats.common.systemproperties.AtsSystemProperties;
import com.axway.ats.core.utils.IoUtils;

/**
 * Operations on S3 storage - Amazon or compliant one.
 * <p>
 * Most of the methods accept object/key names only and they reuse the bucket name specified in constructor.
 * Object keys are case-sensitive and it is recommended to use some naming conventions mentioned
 * <a href="http://docs.aws.amazon.com/AmazonS3/latest/dev/UsingMetadata.html#object-keys" target="_top">here</a>.
 * <br />
 * If some operation fails then {@link S3OperationException} is throws which is a {@link RuntimeException}.
 * This is similar to other rule-based verification clients and is convenient for tests and makes general testing
 * linear like simple script of actions.
 */
@PublicAtsApi
public class S3Operations {

    private static final Logger LOG = Logger.getLogger(S3Operations.class);

    private String              accessKey;
    private String              secretKey;
    private String              endpoint;
    private String              bucketName;
    private String              region;
    private AmazonS3            s3Client;

    /**
     * Connect to S3-compatible service using provided credentials.
     * @param endpoint location where the service is exposed like myhost.example.com:8080
     * @param accessKey access key part of credentials
     * @param secretKey secret key part of credentials
     * @param bucketName name of the bucket to use for object operations
     */
    @PublicAtsApi
    public S3Operations( String endpoint, String accessKey, String secretKey, String bucketName ) {

        this(endpoint, accessKey, secretKey, null, bucketName);
    }

    /**
     * Connect to S3-compatible service using provided credentials.
     * @param endpoint location where the service is exposed like myhost.example.com:8080
     * @param accessKey access key part of credentials
     * @param secretKey secret key part of credentials
     * @param bucketName name of the bucket to use for object operations
     */
    @PublicAtsApi
    public S3Operations( String endpoint, String accessKey, String secretKey, String region, String bucketName ) {

        this.endpoint = endpoint;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.region = region;
        if (bucketName == null) {
            throw new IllegalArgumentException("Bucket name should not be null");
        }
        this.bucketName = bucketName;
        // TODO - create/build S3Client here
    }

    /**
     * Check current specified bucket (the one in constructor) for existence
     */
    @PublicAtsApi
    public boolean doesBucketExist() {

        return doesBucketExist(this.bucketName);
    }

    /**
     * Check bucket for existence
     *
     * @param bucketName the bucket name
     */
    @PublicAtsApi
    public boolean doesBucketExist( String bucketName ) {

        AmazonS3 s3Client = getClient();
        return s3Client.doesBucketExistV2(bucketName);
    }

    /**
     * Get list of of elements matching the specified prefix and pattern
     * @param folderPrefix common prefix of paths that all elements should have (like directory). Use empty string
     *        when you want search from top of the bucket, i.e. "/" should not be used in front
     * @param searchPattern pattern to match - could be object/file name or some regular expression (RegEx)
     * @param searchRecursively should search be recursive
     *
     * @return array of beans with info about each object found
     * @see S3ObjectInfo
     */
    @PublicAtsApi
    public List<S3ObjectInfo> list( String folderPrefix, String searchPattern, boolean searchRecursively ) {

        return listBucket(folderPrefix, searchPattern, searchRecursively);
    }

    /**
     * Delete object
     * @param object name/key of the object to be deleted
     */
    @PublicAtsApi
    public void deleteObject( String objectName ) {

        AmazonS3 s3Client = getClient();
        s3Client.deleteObject(bucketName, objectName);
        LOG.info("Deleted object '" + objectName + "' from bucket '" + bucketName + "'");
    }

    /**
     * Delete the bucket specified in constructor
     *
     * @throws S3OperationException in case of a client or server error
     */
    @PublicAtsApi
    public void deleteBucket() throws S3OperationException {

        AmazonS3 s3Client = getClient();
        // TODO: check for direct method
        // remove all Objects before deleting the bucket
        for (S3ObjectInfo element : listBucket("" /* all */, ".*", true)) {
            deleteObject(element.getName());
        }

        s3Client.deleteBucket(bucketName);
        LOG.info("Deleted bucket '" + bucketName + "'");
    }

    /**
     * Create a bucket with the pointed name
     *
     * @param bucketName the name of the bucket that should be created
     */
    @PublicAtsApi
    public void createBucket() {

        LOG.info("Create Bucket '" + bucketName + "'");
        getClient().createBucket(bucketName);
    }

    /**
     * Get MD5, size, owner, storage class and last modification time for a desired file in the pointed bucket
     *
     * @param fileName the file name
     */
    @PublicAtsApi
    public S3ObjectInfo getFileMetadata( String fileName ) {

        S3ObjectSummary element = getBucketElement("", ".*", true, fileName);
        if (element != null) {
            S3ObjectInfo s3Info = new S3ObjectInfo(element);
            return s3Info;
        } else {
            throw new NoSuchElementException("File with name '" + fileName + "' does not exist!");
        }
    }

    /**
     * Get size of the specified object/file
     *
     * @param objectName the object name
     * @return the size of the object in bytes
     */
    @PublicAtsApi
    public long getFileSize( String objectName ) {

        S3ObjectSummary element = getBucketElement("", ".*", true, objectName);
        if (element != null) {
            return element.getSize();
        } else {
            throw new NoSuchElementException("Object with name '" + objectName + "' does not exist!");
        }
    }

    /**
     * Get MD5 for a desired file in the pointed bucket
     *
     * @param objectName the object/file name
     * @return HEX-based MD5 sum of the object like <code>e598833161abb9b25b1c3390987e691a</code>.
     *         Amazon S3 API also refers this value as ETag.
     */
    @PublicAtsApi
    public String getFileMD5( String objectName ) {

        S3ObjectSummary element = getBucketElement("", ".*", true, objectName);
        if (element != null) {
            return element.getETag();
        } else {
            throw new NoSuchElementException("Object with name '" + objectName + "' does not exist!");
        }
    }

    /**
     * Get last modification time for specified object/file
     *
     * @param objectName the object/file name
     * @return Date of last modification time
     */
    @PublicAtsApi
    public Date getFileModificationTime( String objectName ) {

        S3ObjectSummary element = getBucketElement("", ".*", true, objectName);
        if (element != null) {
            return element.getLastModified();
        } else {
            throw new NoSuchElementException("Object with name '" + objectName + "' is not found in bucket '"
                                             + bucketName + "'!");
        }
    }

    /**
     * Upload a text file
     *
     * @param objectName the object name ( key) for uploaded data
     * @param sourceFile the file, that should be uploaded
     */
    @PublicAtsApi
    public void uploadAsText( String objectName, String sourceFile ) {

        try {
            getClient().putObject(bucketName, objectName, fileToString(sourceFile));
        } catch (Exception e) {
            handleExeption(e, "Upload error. If error persists check your endpoint, credentials and permissions.");
        }
    }

    /**
     * Upload a file to the S3 storage
     *
     * @param targetObjectName the target name of the object/file
     * @param sourceFileName the name of local file, that should be uploaded
     */
    @PublicAtsApi
    public void upload( String targetObjectName, String sourceFileName ) {

        File localFile = new File(sourceFileName);
        if (!localFile.exists() || !localFile.isFile()) {
            throw new IllegalArgumentException(sourceFileName + " does not exist");
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Start uploading file '" + sourceFileName + "' to bucket '" + bucketName + "'");
        }
        try {
            getClient().putObject(bucketName, targetObjectName, new File(sourceFileName));
        } catch (Exception e) {
            handleExeption(e, "File upload error. If error persists check your endpoint, credentials and permissions.");
        }
        LOG.info("Uploaded file '" + sourceFileName + "' as object named '" + targetObjectName + "' into bucket "
                 + bucketName);
    }

    /**
     * Upload a file with metadata.
     *
     * @param targetObjectName the name of the file that will be uploaded
     * @param sourceInputStream the InputStream of the data that should be uploaded. It might be closed after reading.
     */
    public void uploadFile( String targetObjectName, InputStream sourceInputStream ) {

        try {
            getClient().putObject(bucketName, targetObjectName, sourceInputStream, null);
        } catch (Exception e) {
            handleExeption(e, "File upload error. If error persists check your endpoint, credentials and permissions.");
        }
    }

    /**
     * Download an object data as a file
     *
     * @param remoteObjectName the name of object/key which contents should be downloaded
     * @param localFileName the location and file name on the local machine, where the file will be downloaded
     * @throws S3OperationException if there is an error during data transfer
     */
    @PublicAtsApi
    public void download( String remoteObjectName, String localFileName ) throws S3OperationException,
                                                                          IllegalArgumentException {

        AmazonS3 s3Client = getClient();

        localFileName = IoUtils.normalizeFilePath(localFileName);
        String localDirName = IoUtils.getFilePath(localFileName);
        String localFileOnlyName = IoUtils.getFileName(localFileName);
        File localDir = new File(localDirName);
        if (localDir.exists()) {
            if (localDir.isFile()) {
                throw new IllegalArgumentException("Could not create file " + localFileOnlyName + " into existing file "
                                                   + localDirName);
            }
            // else dir exists
        } else {
            LOG.debug("Creating target directory path " + localDirName);
            if (!localDir.mkdirs()) {
                throw new S3OperationException("Could not create local directory path '" + localDirName
                                               + "' for local file specified '" + localFileName + "'");
            }
        }

        S3Object obj = s3Client.getObject(bucketName, remoteObjectName);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File(localFileName)));
                S3ObjectInputStream s3is = obj.getObjectContent();) {
            byte[] readBuffArr = new byte[4096];
            int readBytes = 0;
            while ( (readBytes = s3is.read(readBuffArr)) >= 0) {
                bos.write(readBuffArr, 0, readBytes);
            }
        } catch (Exception e) {
            handleExeption(e, "Error while downloading object " + remoteObjectName + " to local file " + localFileName
                              + ". If error persists check your endpoint, credentials and permissions.");
        }
        LOG.info("S3 object '" + remoteObjectName + "; is downloaded successfully from bucket '" + bucketName
                 + "' to file " + localFileName);
    }

    /**
     * Get object/file contents from the remote storage.
     * <p>
     * <em>Note</em> that user should close stream after reading in order to release resources and do not
     *   block connection from the pool.
     * </p>
     * @param objectName the object/key name that should be downloaded
     * @return InputStream that could be used to download object contents
     */
    @PublicAtsApi
    public InputStream download( String objectName ) {

        AmazonS3 s3Client = getClient();

        S3Object o = s3Client.getObject(bucketName, objectName);
        return o.getObjectContent();
    }

    /**
     * Move or rename file from one bucket to another
     *
     * @param fromBucket the bucket name, where the file is currently located
     * @param toBucket the bucket name, where the file will be moved
     * @param file the name of the file, that will be moved
     */
    @PublicAtsApi
    public void move( String fromBucket, String toBucket, String file ) {

        AmazonS3 s3Client = getClient();

        try {
            s3Client.copyObject(fromBucket, file, toBucket, file);
            s3Client.deleteObject(fromBucket, file);
        } catch (Exception e) {
            handleExeption(e, "S3 object move error");
        }
    }

    /**
     * Handle exceptions of Amazon APIs
     */
    private void handleExeption( Exception e, String optionalMsg ) throws S3OperationException {

        StringBuilder sb = new StringBuilder();
        if (optionalMsg != null) {
            sb.append(optionalMsg + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
        }
        if (e instanceof AmazonServiceException) {
            AmazonServiceException ase = (AmazonServiceException) e;
            sb.append("Caught an AmazonServiceException (returned error from the server):");
            sb.append("Error Message:    " + ase.getMessage() + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            sb.append("HTTP Status Code: " + ase.getStatusCode() + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            sb.append("AWS Error Code:   " + ase.getErrorCode() + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            sb.append("Error Type:       " + ase.getErrorType() + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            sb.append("Request ID:       " + ase.getRequestId() + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
        } else if (e instanceof AmazonClientException) {
            sb.append("Caught an AmazonClientException, which " +
                      "means the client encountered an internal error while trying to " +
                      "reach S3 storage server like network error" + AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            sb.append("Error Message: " + e.getMessage());
        } else {
            sb.append(e.getMessage()); // normal exception
        }
        throw new S3OperationException(sb.toString(), e);
    }

    /**
     * Gets configured AmazonS3 client instance. Does not perform actual request until first remote data is needed
     */
    private AmazonS3 getClient() {

        if (s3Client != null) {
            return s3Client; // already cached
        }

        ClientConfiguration config = new ClientConfiguration();
        if (endpoint != null && endpoint.startsWith("https://")) {
            config.setProtocol(Protocol.HTTPS);
        } else {
            config.setProtocol(Protocol.HTTP);
        }

        BasicAWSCredentials creds = new BasicAWSCredentials(accessKey, secretKey);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Creating S3 client to " + ( (endpoint == null)
                                                                      ? "default Amazon"
                                                                      : endpoint)
                      + " endpoint with access key " + accessKey);
        }

        if (this.endpoint != null) {
            if (region == null || region.trim().length() == 0) {
                region = Regions.DEFAULT_REGION.name();
            }
            s3Client = AmazonS3ClientBuilder.standard()
                                            .withCredentials(new AWSStaticCredentialsProvider(creds))
                                            .withEndpointConfiguration(new EndpointConfiguration(endpoint, region))
                                            .withClientConfiguration(config)
                                            .withPathStyleAccessEnabled(true)
                                            .build();
        } else {
            s3Client = AmazonS3ClientBuilder.standard()
                                            .withCredentials(new AWSStaticCredentialsProvider(creds))
                                            .withClientConfiguration(config)
                                            .withPathStyleAccessEnabled(true)
                                            .build();
        }
        return s3Client;
    }

    private String fileToString( String fileName ) {

        String line = null;
        StringBuilder stringBuilder = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            while ( (line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(AtsSystemProperties.SYSTEM_LINE_SEPARATOR);
            }

            return stringBuilder.toString();
        } catch (Exception e) {
            throw new S3OperationException("Local file '" + fileName + "' could not be read!");
        }
    }

    /**
     * Used for S3Operations only methods
     * @param folderPrefix should be full path w/o "/" in front
     * @param searchString
     * @param recursive
     * @param searchedFile
     * @return
     */
    private S3ObjectSummary getBucketElement( String folderPrefix, String searchString, boolean recursive,
                                              String searchedFile ) {

        if (folderPrefix.startsWith("/")) {
            folderPrefix = folderPrefix.substring(1, folderPrefix.length());
            LOG.warn("Folder prefix argument should not start with slash \"/\". It is cut to " + folderPrefix);
        }

        // TODO: optimize this to directly getObject() and then retrieve needed metadata or use key directly as prefix
        //s3Client.getObject(bucketName, searchedFile).getObjectMetadata().
        try {
            ObjectListing objectListing = getClient().listObjects(bucketName, searchedFile);
            while (true) { // reused block for multiple matches
                for (Iterator<?> iterator = objectListing.getObjectSummaries()
                                                         .iterator(); iterator.hasNext();) {
                    S3ObjectSummary el = (S3ObjectSummary) iterator.next();
                    if (searchedFile.equals(el.getKey())) {
                            return el;
                    }
                    /*if (searchedFile.equals(el.getKey())
                        && isImmediateDescendant(folderPrefix, "/" + el.getKey(), searchString, recursive)) {
                        return el;
                    }*/
                }
                // are there more results to retrieve( paging)
                if (objectListing.isTruncated()) {
                    objectListing = s3Client.listNextBatchOfObjects(objectListing);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            handleExeption(e, "Get S3 object error.");
        }

        return null;
    }

    /**
     *
     * @param folderPrefix
     * @param searchString
     * @param recursive
     * @return
     */
    private List<S3ObjectInfo> listBucket( String folderPrefix, String searchString, boolean recursive ) {

        List<S3ObjectInfo> allListElements = new ArrayList<S3ObjectInfo>();

        //Alternative but not documented in S3 API: getClient().listObjectsV2(bucket, "prefix")
        ObjectListing objectListing = getClient().listObjects(bucketName, folderPrefix);
        int i = 0;
        Pattern searchStringPattern = Pattern.compile(searchString);
        while (true) {
            for (Iterator<?> iterator = objectListing.getObjectSummaries()
                                                     .iterator(); iterator.hasNext();) {
                S3ObjectSummary objectSummary = (S3ObjectSummary) iterator.next();
                if (LOG.isTraceEnabled()) {
                    LOG.trace("listObjects(" +(++i) +"): " +  objectSummary.toString());
                }
                //TODO: optimize if search for .* in root and recursively: listObjects(bucketName)
                if (isImmediateDescendant(folderPrefix, objectSummary.getKey(), searchStringPattern, recursive)) {
                    allListElements.add(new S3ObjectInfo(objectSummary));
                }
            }

            // more objectListing retrieve?
            if (objectListing.isTruncated()) {
                objectListing = s3Client.listNextBatchOfObjects(objectListing);
            } else {
                break;
            }
        }

        return allListElements;
    }

    private boolean isImmediateDescendant( String pathPrefix, String key, Pattern searchStringPattern,
                                           boolean recursive ) {

        // check if the file is in the specified directory (prefix) or beneath
        if (!key.startsWith(pathPrefix)) {
            return false;
        }

        String keyNameSuffix = key.substring(pathPrefix.length());
        String[] fileTokens = keyNameSuffix.split("/");

        if (!recursive) {
            if (fileTokens.length == 1) {
                Matcher matcher = searchStringPattern.matcher(fileTokens[0]);
                // check if the pattern matches the given search value
                if (!matcher.find()) {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            for (String fileToken : fileTokens) {
                Matcher matcher = searchStringPattern.matcher(fileToken);
                // check if the pattern matches the given search value
                if (!matcher.find()) {
                    return false;
                }
            }
        }

        return true;
    }
}
