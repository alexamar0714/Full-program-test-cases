package amberdb.model;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import amber.checksum.Checksum;
import amber.checksum.ChecksumAlgorithm;
import amberdb.AbstractDatabaseIntegrationTest;
import doss.core.Writables;
import org.bouncycastle.util.encoders.Hex;
import org.h2.store.fs.FilePath;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import amberdb.AmberSession;
import amberdb.enums.MaterialType;


public class FileTest extends AbstractDatabaseIntegrationTest {

    @Test
    public void shouldReturn0InAbsenceOfFileSize() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        assertEquals(0L, file.getFileSize());
        assertEquals(0L, file.getSize());
    }

    @Test
    public void shouldSetFileSizeOnSetBlobId() throws IOException, NoSuchAlgorithmException {
        Work work = amberSession.addWork();

        Copy copy = work.addCopy();
        File file = copy.addFile();
        file.put(Writables.wrap("TEXT"));
        assertEquals(4L, file.getFileSize());
        assertEquals(4L, file.getSize());

        // replace file
        file.put(Writables.wrap("TEXT5"));
        assertEquals(5L, file.getFileSize());
        assertEquals(5L, file.getSize());

        // point to non-existent blob
        file.setBlobIdAndSize(0L);
        assertEquals(0L, file.getFileSize());
        assertEquals(0L, file.getSize());

        copy = work.addCopy();
        file = copy.addFile();
        file.putLegacyDoss(Paths.get("src/test/resources/hello.txt"));
        assertEquals(5L, file.getFileSize());
        assertEquals(5L, file.getSize());

        copy = work.addCopy();
        file = copy.addFile();
        byte[] testFile = new byte[] {1,2,3,4,5,6};
        byte[] cs = MessageDigest.getInstance("sha1").digest(testFile);
        file.putWithChecksumValidation(Writables.wrap(testFile),
                new Checksum(ChecksumAlgorithm.fromString("sha1"),
                        org.apache.commons.codec.binary.Hex.encodeHexString(cs)));
        assertEquals(6L, file.getFileSize());
        assertEquals(6L, file.getSize());


    }

    @Test
    public void shouldReturnTheChecksumCreationDate() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addFile();
        
        Date date = new Date();
        file.setChecksumGenerationDate(date);
        assertEquals(date, file.getChecksumGenerationDate());
    }
    
    @Test
    public void shouldResetTechnicalProperties() {
        Work work = amberSession.addWork();
        Copy copy = work.addCopy();
        File file = copy.addImageFile();
        ImageFile imageFile = (ImageFile) file;
        long blobId = 1L;
        String mimeType = "image/jpg";
        String fileName = "image_file_name.jpg";
        String fileFormat = "Image";
        String fileFormatVersion = "1.0";
        long fileSize = 9000L;
        String compression = "no";
        String checksum = "1x2f90afdanjfdkaj";
        String checksumType = "SHA1";
        Date checksumDate = new Date();
        String device = "printer";
        String deviceSerialNumber = "1sx21";
        String software = "paintpaw";
        String softwareSerialNumber = "1s3s";
        String encoding = "utf8";
        String dcmCopyPid = "nla.aus-122325";
        
        String resolution = "400 x 400";
        String resolutionUnit = "dpi";
        String colourSpace = "colour space";
        String orientation = "orientation";
        int imageWidth = 2031;
        int imageLength = 3802;
        String manufacturerMake = "manufacturer make";
        String manufacturerName = "manufacturer name";
        String manufacturerSerialNumber = "manufacturer serial number";
        Date applicationDateCreated = new Date();
        String application = "application";
        Date dateDigitised = new Date();
        String samplesPerPixel = "samples per pixel";
        String bitDepth = "bit depth";
        String photometric = "photometric";
        String location = "location";
        String colourProfile = "colour profile";
        String cpLocation = "cp location";
        String zoomLevel = "zoom level";
        
        imageFile.setBlobIdAndSize(blobId);
        imageFile.setMimeType(mimeType);
        imageFile.setFileName(fileName);
        imageFile.setFileFormat(fileFormat);
        imageFile.setFileFormatVersion(fileFormatVersion);
        imageFile.setFileSize(fileSize);
        imageFile.setCompression(compression);
        imageFile.setChecksum(checksum);
        imageFile.setChecksumType(checksumType);
        imageFile.setChecksumGenerationDate(checksumDate);
        imageFile.setDevice(device);
        imageFile.setDeviceSerialNumber(deviceSerialNumber);
        imageFile.setSoftware(software);
        imageFile.setSoftwareSerialNumber(softwareSerialNumber);
        imageFile.setEncoding(encoding);
        imageFile.setDcmCopyPid(dcmCopyPid);
        imageFile.setResolution(resolution);
        imageFile.setResolutionUnit(resolutionUnit);
        imageFile.setColourSpace(colourSpace);
        imageFile.setOrientation(orientation);
        imageFile.setImageWidth(imageWidth);
        imageFile.setImageLength(imageLength);
        imageFile.setManufacturerMake(manufacturerMake);
        imageFile.setManufacturerModelName(manufacturerName);
        imageFile.setManufacturerSerialNumber(manufacturerSerialNumber);
        imageFile.setApplicationDateCreated(applicationDateCreated);
        imageFile.setApplication(application);
        imageFile.setDateDigitised(dateDigitised);
        imageFile.setSamplesPerPixel(samplesPerPixel);
        imageFile.setBitDepth(bitDepth);
        imageFile.setPhotometric(photometric);
        imageFile.setLocation(location);
        imageFile.setColourProfile(colourProfile);
        imageFile.setCpLocation(cpLocation);
        imageFile.setZoomLevel(zoomLevel);
        
        assertTrue(blobId == file.getBlobId());
        assertEquals(mimeType, file.getMimeType());
        assertEquals(fileName, file.getFileName());
        assertEquals(fileFormat, file.getFileFormat());
        assertEquals(fileFormatVersion, file.getFileFormatVersion());
        assertEquals(fileSize, file.getFileSize());
        assertEquals(compression, file.getCompression());
        assertEquals(checksum, file.getChecksum());
        assertEquals(checksumType, file.getChecksumType());
        assertEquals(checksumDate,file.getChecksumGenerationDate());
        assertEquals(device, file.getDevice());
        assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        assertEquals(software, file.getSoftware());
        assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        assertEquals(encoding, file.getEncoding());
        assertEquals(dcmCopyPid, file.getDcmCopyPid());
        assertEquals(resolution, ((ImageFile) file).getResolution());
        assertEquals(resolutionUnit, ((ImageFile) file).getResolutionUnit());
        assertEquals(colourSpace, ((ImageFile) file).getColourSpace());
        assertEquals(orientation, ((ImageFile) file).getOrientation());
        assertTrue(imageWidth == ((ImageFile) file).getImageWidth());
        assertTrue(imageLength == ((ImageFile) file).getImageLength());
        assertEquals(manufacturerMake, ((ImageFile) file).getManufacturerMake());
        assertEquals(manufacturerName, ((ImageFile) file).getManufacturerModelName());
        assertEquals(manufacturerSerialNumber, ((ImageFile) file).getManufacturerSerialNumber());
        assertEquals(applicationDateCreated, ((ImageFile) file).getApplicationDateCreated());
        assertEquals(application, ((ImageFile) file).getApplication());
        assertEquals(dateDigitised, ((ImageFile) file).getDateDigitised());
        assertEquals(samplesPerPixel, ((ImageFile) file).getSamplesPerPixel());
        assertEquals(bitDepth, ((ImageFile) file).getBitDepth());
        assertEquals(photometric, ((ImageFile) file).getPhotometric());
        assertEquals(colourProfile,((ImageFile) file).getColourProfile());
        assertEquals(cpLocation,((ImageFile) file).getCpLocation());
        assertEquals(location, ((ImageFile) file).getLocation());
        assertEquals(zoomLevel, ((ImageFile) file).getZoomLevel());
        assertTrue(file instanceof ImageFile);
        
        file = file.resetTechnicalProperties(MaterialType.TEXT);
        assertTrue(file instanceof File);
        assertTrue(blobId == file.getBlobId());
        assertEquals(mimeType, file.getMimeType());
        assertEquals(fileName, file.getFileName());
        assertEquals(fileFormat, file.getFileFormat());
        assertEquals(fileFormatVersion, file.getFileFormatVersion());
        assertEquals(fileSize, file.getFileSize());
        assertEquals(compression, file.getCompression());
        assertEquals(checksum, file.getChecksum());
        assertEquals(checksumType, file.getChecksumType());
        assertEquals(checksumDate,file.getChecksumGenerationDate());
        assertEquals(device, file.getDevice());
        assertEquals(deviceSerialNumber, file.getDeviceSerialNumber());
        assertEquals(software, file.getSoftware());
        assertEquals(softwareSerialNumber, file.getSoftwareSerialNumber());
        assertEquals(encoding, file.getEncoding());
        assertEquals(dcmCopyPid, file.getDcmCopyPid());
        
        file = file.resetTechnicalProperties(MaterialType.IMAGE);
        imageFile = (ImageFile) file;
        assertNull(imageFile.getResolution());
        assertNull(imageFile.getResolutionUnit());
        assertNull(imageFile.getColourSpace());
        assertNull(imageFile.getOrientation());
        assertNull(imageFile.getImageWidth());
        assertNull(imageFile.getImageLength());
        assertNull(imageFile.getManufacturerMake());
        assertNull(imageFile.getManufacturerModelName());
        assertNull(imageFile.getManufacturerSerialNumber());
        assertNull(imageFile.getApplicationDateCreated());
        assertNull(imageFile.getApplication());
        assertNull(imageFile.getDateDigitised());
        assertNull(imageFile.getSamplesPerPixel());
        assertNull(imageFile.getBitDepth());
        assertNull(imageFile.getPhotometric());
        assertNull(imageFile.getColourProfile());
        assertNull(imageFile.getCpLocation());
        assertNull(imageFile.getLocation());
        assertNull(imageFile.getZoomLevel());
    }
}
