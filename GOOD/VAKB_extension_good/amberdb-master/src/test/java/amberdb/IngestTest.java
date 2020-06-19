package amberdb;

import amberdb.enums.CopyRole;
import amberdb.model.Page;
import amberdb.model.Work;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.*;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


public class IngestTest {
    @ClassRule
    public static TemporaryFolder folder = new TemporaryFolder();

    private static AmberSession db;
    private static JobMockup job;
    private static String samplePI;

    @BeforeClass
    public static void setup() throws IOException {
        job = uploadFiles();
        db = identifyWorks();
    }

    @AfterClass
    public static void teardown() throws IOException {
        job = null;
        if (db != null) {
            db.close();
        }
        db = null;
    }

    private static JobMockup uploadFiles() throws IOException {
        job = new JobMockup();

        // Note: banjo upload files step does not interact with amberDb at all.
        List<Path> list = new ArrayList<Path>();
        list.add(TestUtils.newDummyFile(folder, "a.tiff"));
        list.add(TestUtils.newDummyFile(folder, "b.tiff"));

        list.add(TestUtils.newDummyFile(folder, "nla.aus-vn12345-1.tiff"));
        list.add(TestUtils.newDummyFile(folder, "nla.aus-vn12345-2.tiff"));
        list.add(TestUtils.newDummyFile(folder, "nla.aus-vn12345.xml"));

        list.add(TestUtils.newDummyFile(folder, "nla.aus-vn643643-1.tiff"));
        list.add(TestUtils.newDummyFile(folder, "nla.aus-vn643643-2.tiff"));

        job.files = list;
        return job;
    }

    private static AmberSession identifyWorks() throws IOException {
        AmberSession amberDb = new AmberSession();

        List<Long> bookIds = new ArrayList<Long>();
        // try to detect works based on filenames,

        Work auto1 = amberDb.addWork();
        samplePI = auto1.getObjId();

        auto1.setBibId("12345");
        auto1.addPage(job.files.get(6), "image/tiff").setOrder(1);

        Page page = auto1.addPage();
        page.setOrder(2);
        page.addCopy(job.files.get(2), CopyRole.MASTER_COPY, "image/tiff");
        page.addCopy(job.files.get(4), CopyRole.OCR_METS_COPY, "text/xml");

        auto1.addPage(job.files.get(3), "image/tiff").setOrder(3);

        auto1.setTitle("Blinky Bill");
        bookIds.add(auto1.getId());

        Work auto2 = amberDb.addWork();
        auto2.setBibId("55555");
        auto2.addPage(job.files.get(5), "image/tiff").setOrder(1);
        auto2.setTitle("James and the giant peach");

        bookIds.add(auto2.getId());

        // user manually creates a work out of the first two pages

        Work manual = amberDb.addWork();
        manual.addPage(job.files.get(0), "image/tiff").setOrder(1);
        manual.addPage(job.files.get(1), "image/tiff").setOrder(2);
        manual.setTitle("Little red riding hood");

        bookIds.add(manual.getId());
        job.workIds = bookIds;

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(amberDb.suspend());

        return amberDb;
    }

    @Test
    public void testFindWorkByPI() {
        Work sample = db.findWork(PIUtil.parse(samplePI));
        assertEquals("Blinky Bill", sample.getTitle());

        String resultPI = sample.getObjId();
        assertEquals(samplePI, resultPI);
    }

    @Test
    public void testDescribeWorks() {

        for (Long workId : job.getWorks()) {
            describeWork(job, db.findWork(workId));
        }

        // save this transaction without committing it
        // so we can continue manipulating these objects
        // in another web request
        job.setAmberTxId(db.suspend());
    }

    @Test
    public void testFixLabel() {
        Work work = db.findWorkByVn(12345);

        try {
            int count = work.countParts();
            if (count > 0) {
                Page page7 = work.getPages().iterator().next();
                if (page7 != null)
                    page7.setTitle("III");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.commit();
    }

    @Test
    public void testCancelJob() {
        db.rollback();
    }

    @Ignore
    @Test
    public void testSerializeToJson() throws JsonGenerationException, JsonMappingException,
            IOException {
        System.out.println(new ObjectMapper()
                .writerWithDefaultPrettyPrinter()
                .writeValueAsString(
                        db.serializeToJson()));
    }

    private void describeWork(JobMockup job, Work work) {
        // fill in default values
        for (Page p : work.getPages()) {
            assertNotNull(p.getId());
        }

        // show the qa form
        try {

            Page p1;
            Page p2;

            if (work.countParts() > 1) {

                p1 = work.getPage(1);
                p2 = work.getPage(2);

                Page a = work.getPage(1);
                Page b = work.getPage(2);

                a.setOrder(2);
                b.setOrder(1);

                assertEquals(p1, work.getPage(2));
                assertEquals(p2, work.getPage(1));
            }

            if (work.countParts() > 0) {
                Page page = work.getPage(1);
                page.setTitle("IV");
            }

            assertEquals(work.getPage(1).getTitle(), "IV");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
