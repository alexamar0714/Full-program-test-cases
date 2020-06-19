package amberdb.sql;

import amberdb.AmberDb;
import amberdb.AmberSession;
import org.h2.jdbcx.JdbcConnectionPool;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AmberLookupsTest {
    private AmberSession session;
    private AmberDb adb;
    private Lookups lookups;
    
    @Rule
    public TemporaryFolder folder = new TemporaryFolder();

    @Before
    public void setup() {
        adb = new AmberDb(JdbcConnectionPool.create("jdbc:h2:"+folder.getRoot()+"persist;DATABASE_TO_UPPER=false","lookups","lookups"), folder.getRoot().getAbsolutePath());
        session = adb.begin();
        lookups = session.getLookups();
    }

    @After
    public void teardown() throws IOException {
        if (session != null)
            session.close();
    }

    @Test
    public void testAddLookupEntry() {
        Long entry = lookups.addLookup("testAddition", "entry", "entry");
        Long entry1 = lookups.addLookup("testAddition", "entry", "entry");
        ListLu lu = lookups.findLookup(entry);
        ListLu lu1 = lookups.findLookup(entry1);
        assertEquals("entry", lu.getCode());
        assertEquals("entry_1", lu1.getCode());
    }
    
    @Test
    public void testFindActiveDevices() {
        List<ToolsLu> devices = lookups.findActiveToolsFor("toolCategory", "Device");
        assertNotNull(devices);
        assertEquals(devices.size(), 10);
    }

    @Test
    public void testNaturalSort() {
        List<ListLu> values = lookups.findActiveLookupsFor("carrierCapacity");
        assertNotNull(values);
        String[] actuals = populateArray(values);
        String[] expected = { "2 min", "4.7 GB", "5 min", "12 min", "30 min", "40 min", "45 min", "46 min", "60 min", "63 min", "65 min", "74 min", "80 min", "90 min", "95 min", "96 min", "120 min", "122 min", "125 min", "180 min", "650 MB" };
        assertArrayEquals(expected, actuals);

        values = lookups.findActiveLookupsFor("speed");
        actuals = populateArray(values);
        String[] expectedSpeed = { "2.38 cm/s", "4.76 cm/s", "9.5 cm/s", "19.05 cm/s", "38.1 cm/s", "76.2 cm/s" };
        assertArrayEquals(expectedSpeed, actuals);

        values = lookups.findActiveLookupsFor("reelSize");
        actuals = populateArray(values);
        String[] expectedReelSize = { "2 min", "2in", "3in IEC", "4.7 GB", "4in IEC", "5 min", "5in IEC", "5in NAB", "6in IEC", "7in IEC", "7in NAB", "8.25in IEC", 
                "10in IEC", "10in NAB", "12 min", "30 min", "40 min", "45 min", "46 min", "60 min", "63 min", "65 min", "74 min", "80 min", "90 min", "95 min", 
                "96 min", "120 min", "122 min", "125 min", "180 min", "650MB"};
        assertArrayEquals(expectedReelSize, actuals);

        values = lookups.findActiveLookupsFor("channel");
        actuals = populateArray(values);
        String[] expectedChannel = { "0.5", "1", "2", "4", "8", "16", "24" };      
        assertArrayEquals(expectedChannel, actuals);

        values = lookups.findActiveLookupsFor("constraint");
        actuals = populateArray(values);
        String[] expectedConstraint = {"Adult content", "Closed", "Copyright", "Error during migration", "Free-of-charge", "Indigenous", "Indigenous - male", "OH/SPATS", "Pending", "Thumbnails only", "Written permission required"};
        assertArrayEquals(expectedConstraint, actuals);

        values = lookups.findActiveLookupsFor("tempHolding");
        actuals = populateArray(values);
        String[] expectedTempHolding = {"No", "Yes"};
        assertArrayEquals(expectedTempHolding, actuals);

        values = lookups.findActiveLookupsFor("sensitiveMaterial");
        actuals = populateArray(values);
        String[] expectedSensitiveMaterial = {"No", "Yes"};
        assertArrayEquals(expectedSensitiveMaterial, actuals);

        values = lookups.findActiveLookupsFor("digitalSourceType");
        actuals = populateArray(values);
        String[] expectedDigitalSourceType = {"Created by software", "Digitised from a negative on film",
                                              "Digitised from a positive on film",
                                              "Digitised from a print on non-transparent medium",
                                              "Original digital capture of a real life scene"};
        assertArrayEquals(expectedDigitalSourceType, actuals);

        values = lookups.findActiveLookupsFor("sensitiveReason");
        actuals = populateArray(values);
        String[] expectedSensitiveReason = {"Disturbing content", "Indigenous - community only",
                                            "Indigenous - female only", "Indigenous - male only",
                                            "Offensive content"};
        assertArrayEquals(expectedSensitiveReason, actuals);
        
        values = lookups.findActiveLookupsFor("subUnitType");
        actuals = populateArray(values);
        assertTrue("sub unit type should contain article", Arrays.asList(actuals).contains("Article"));
    }
    
    private String[] populateArray(List<ListLu> values) {
        String[] actuals = new String[values.size()];
        for (int i = 0; i < values.size(); i++) {
            actuals[i] = values.get(i).getValue();
        }
        
        return actuals;
    }

    @Test
    public void testFindActiveScanners() {
        List<ToolsLu> scanners = lookups.findActiveToolsFor("toolType", "scanner");
        assertNotNull(scanners);
        System.out.println("devices size " + scanners.size());
        assertEquals(scanners.size(), 10);
    }

    @Test
    public void testFindActiveToolsForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findActiveToolsFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert (nikonEntries.size() == 2);
    }

    @Test
    public void testFindToolsEverRecordedForNikonInInitialSeeds() {
        List<ToolsLu> nikonEntries = lookups.findToolsEverRecordedFor("name", "Nikon");
        assertNotNull(nikonEntries);
        assert (nikonEntries.size() == 2);
    }

    @Test
    public void testFindActiveToolCannon20D() {
        long id = 9L;
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert (cannon20D.getName().equals("Canon 20D"));
    }

    @Test(expected = RuntimeException.class)
    public void testExceptionForNonExistentTool() {
        Long id = 0L;
        lookups.findActiveTool(id);
    }

    @Test
    public void testUpdateToolCannon20DResolution() {
        long id = 9L;
        ToolsLu cannon20D = lookups.findActiveTool(id);
        assert (cannon20D.getName().equals("Canon 20D"));
        assertNull(cannon20D.getResolution());
        cannon20D.setResolution("3504x2336");
        lookups.updTool(cannon20D);
        session.commit();
        ToolsLu updedCannon = lookups.findActiveTool(id);
        assert (updedCannon.getName().equals("Canon 20D"));
        assert (updedCannon.getResolution().equals("3504x2336"));
    }

    @Test
    public void testUpdateToolCannon20DToolType() {
        long canonId = 9L;
        long oldToolTypeId = lookups.findActiveLookup("toolType", "Transmission scanner").get(0).getId();
        long newToolTypeId = lookups.findActiveLookup("toolType", "Reflective scanner").get(0).getId();

        ToolsLu canon = lookups.findTool(canonId);
        assertEquals(oldToolTypeId, canon.getToolTypeId().longValue());
        canon.setToolTypeId(newToolTypeId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assertEquals(newToolTypeId, newCanon.getToolTypeId().longValue());
    }

    @Test
    public void testUpdateToolCannon20DMaterialType() {
        long canonId = 9L;
        long oldMaterialId = lookups.findActiveLookup("materialType", "Image").get(0).getId();
        long newMaterialId = lookups.findActiveLookup("materialType", "Sound").get(0).getId();

        ToolsLu canon = lookups.findTool(canonId);
        assert (canon.getMaterialTypeId() == oldMaterialId);
        canon.setMaterialTypeId(newMaterialId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assert (newCanon.getMaterialTypeId() == newMaterialId);
    }

    @Test
    public void testUpdateToolCannon20DToolCategory() {
        long canonId = 9L;
        long deviceId = lookups.findActiveLookup("toolCategory", "d").get(0).getId();
        long softwareId = lookups.findActiveLookup("toolCategory", "s").get(0).getId();

        ToolsLu canon = lookups.findTool(canonId);
        assert (canon.getToolCategoryId() == deviceId);
        canon.setToolCategoryId(softwareId);
        lookups.updTool(canon);
        ToolsLu newCanon = lookups.findTool(canonId);
        assert (newCanon.getToolCategoryId() == softwareId);
    }

    @Test
    public void testUpdateMaterialTypeTextToOcrText() {
        ListLu imageMT = lookups.findLookup("materialType", "Text");
        imageMT.setValue("Ocr Text");
        lookups.updateLookup(imageMT.id, imageMT.value);
        assertFalse(activeLookupsInclude("materialType", "Text"));
        assert (activeLookupsInclude("materialType", "Ocr Text"));
    }

    private boolean activeLookupsInclude(String name, String value) {
        List<ListLu> values = lookups.findActiveLookupsFor(name);
        if (values == null || values.isEmpty())
            return false;
        for (ListLu luValue : values) {
            if (luValue.getValue() != null && luValue.getValue().equals(value))
                return true;
        }
        return false;
    }

    @Test
    public void testAddMaterialTypeWireless() {
        assertFalse(activeLookupsInclude("materialType", "wireless"));
        ListLu wireless = new ListLu("materialType", "wireless");
        lookups.addLookup(wireless);
        assert (activeLookupsInclude("materialType", "wireless"));
    }

    @Test
    public void testAddSoftwareAdobeIllustrator() {
        ToolsLu ai = new ToolsLu("apaterso");
        ai.setName("Adobe Illustrator CC");
        ai.setSerialNumber("11-220-284");
        ai.setNotes("Use to create clip art");
        lookups.addTool(ai);

        List<ToolsLu> ais = lookups.findActiveToolsFor("name", ai.getName());
        ToolsLu newAI = ais.get(0);
        assert (newAI.getName().equals("Adobe Illustrator CC"));
        assert (newAI.getSerialNumber().equals("11-220-284"));
        assert (newAI.getNotes().equals("Use to create clip art"));
    }
     
    @Test
    public void testUpdateLookupsByCode(){
        ListLu listLu = new ListLu("carrier", "tape 1", "1234");
        lookups.addLookup(listLu);
        ListLu found = lookups.findActiveLookup("carrier", "tape 1").get(0);
        assertEquals(found.getValue(), "1234");
        lookups.updateLookup(found.getId(), "234", "tape 2");
        ListLu updatedLookup = lookups.findLookup(found.getId());
        assertEquals(updatedLookup.getCode(), "tape 2");
        assertEquals(updatedLookup.getValue(), "234");  
    }
}
