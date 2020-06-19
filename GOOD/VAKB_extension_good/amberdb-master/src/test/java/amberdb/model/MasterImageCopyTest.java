package amberdb.model;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import doss.CorruptBlobStoreException;
import doss.local.LocalBlobStore;

import amberdb.AmberDbFactory;
import amberdb.AmberSession;
import amberdb.enums.CopyRole;

public class MasterImageCopyTest {
    private static AmberSession db;
    private static Page coverPageFor341935;
    private static Copy coverPageMasterCopy;
    private static Path tiffUnCompressor = Paths.get("/usr/bin").resolve("touch");
    private static Path jp2Generator = Paths.get("/bin").resolve("echo");
    
    @Before
    public void setup() throws IOException, InstantiationException {
        setTestDataInH2();
    }
    
    @After
    public void teardown() throws IOException {
        if (db != null) db.close();
    }
    
    @Test
    public void testDriveImage() {
        // MasterImageCopy mc = (MasterImageCopy) coverPageFor341935.getCopy(CopyRole.MASTER_COPY);
        // Copy mc = coverPageFor341935.getCopy(CopyRole.MASTER_COPY);
        Copy mc = coverPageMasterCopy;
        //try {
            // mc.deriveImageCopy(tiffUnCompressor, jp2Generator);
        // } catch (IllegalStateException | IOException | InterruptedException e) {
        //    e.printStackTrace();
        //}
    }
    
    private static void setTestDataInH2() {
        db = new AmberSession();
        Work book = db.addWork();
        coverPageFor341935 = book.addPage();
        try {
            coverPageMasterCopy = coverPageFor341935.addCopy(Paths.get(".").resolve("test_tiff.tif"), CopyRole.MASTER_COPY, "image/tiff");
            File file = coverPageMasterCopy.getFile();
            if (file == null)
                System.out.println("file is null");
            else
                System.out.println("file mimetype is " + file.getMimeType());
            db.commit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    private static LocalBlobStore openBlobStore(Path root) {
        try {
            return (LocalBlobStore) LocalBlobStore.open(root);
        } catch (CorruptBlobStoreException e) {
            try {
                LocalBlobStore.init(root);
            } catch (IOException e2) {
                throw new RuntimeException("Unable to initialize blobstore: " + e2.getMessage(), e2);
            }
            return (LocalBlobStore) LocalBlobStore.open(root);
        }
    }
}
