package amberdb.query;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.enums.CopyRole;
import amberdb.model.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.skife.jdbi.v2.DBI;
import org.skife.jdbi.v2.Handle;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WorkChildrenQueryTest extends AbstractDatabaseIntegrationTest {


    @Test
    public void testGetWorkChildren() throws IOException {

        // create a test work with children
        Work parent = buildWork();
        amberSession.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(amberSession);
        List<Work> children = wcq.getChildRange(parent.getId(), 0, 5);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"page " + i);
        }
        
        amberSession.setLocalMode(true); 
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getCopy(CopyRole.MASTER_COPY)
                    .getFile().getDevice(),"device " + i);
        }
        amberSession.setLocalMode(false);
        
        children = wcq.getChildRange(parent.getId(), 5, 5);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"page " + (i+5));
        }

        children = wcq.getChildRange(parent.getId(), 25, 35);
        assertEquals(children.size(), 5);
        for (int i = 0; i < 5; i++) {
            assertEquals(children.get(i).getTitle(),"work " + (i+25));
        }

        int numChilds = wcq.getTotalChildCount(parent.getId());
        assertEquals(30, numChilds);

        List<CopyRole> roles = wcq.getAllChildCopyRoles(parent.getId());
        assertEquals(roles.size(), 2);
        assertTrue(roles.contains(CopyRole.MASTER_COPY));
        assertTrue(roles.contains(CopyRole.ACCESS_COPY));

        amberSession.getAmberGraph().clear();
        children = wcq.getChildRange(parent.getId(), 5, 5);
        amberSession.setLocalMode(true);
        assertEquals(children.size(), 5);
        Work c1 = children.get(0);
        for (Copy c : c1.getCopies()) {
            assertNotNull(c);
            File f = c.getFile();
            assertNotNull(f);
        }
        amberSession.setLocalMode(false);

        List<Section> sections = wcq.getSections(parent.getId());
        amberSession.setLocalMode(true);
        assertEquals(sections.size(), 7);
        amberSession.setLocalMode(false);
    }

    @Test
    public void testGetChildRangeSorted() throws Exception {
        // create a test work with children with properties to sort by
        Work parent = amberSession.addWork();

        // add children with properties that are not in the same sort order as the Order property or each other
        Work child1 = amberSession.addWork();
        child1.setOrder(1);
        child1.setSubUnitType("subUnitType5");
        child1.setSheetName("sheetName2");
        parent.addChild(child1);
        
        Work child2 = amberSession.addWork();
        child2.setOrder(2);
        child2.setSubUnitType("subUnitType4");
        child2.setSheetName("sheetName5");
        parent.addChild(child2);
        
        Work child3 = amberSession.addWork();
        child3.setOrder(3);
        child3.setSubUnitType("subUnitType3");
        child3.setSheetName("sheetName1");
        parent.addChild(child3);
        
        Work child4 = amberSession.addWork();
        child4.setOrder(4);
        child4.setSubUnitType("subUnitType2");
        child4.setSheetName("sheetName4");
        parent.addChild(child4);
        
        Work child5 = amberSession.addWork();
        child5.setOrder(5);
        child5.setSubUnitType("subUnitType1");
        child5.setSheetName("sheetName3");
        child5.setEndDate(new Date());
        child5.setIsMissingPage(true);
        parent.addChild(child5);

        amberSession.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(amberSession);

        // sort by subUnitType should be child 5, 4, 3, 2, 1
        List<Work> sortedBySubUnitType = wcq.getChildRangeSorted(parent.getId(), 0, 10, "subUnitType", true);
        assertEquals(child5.getId(), sortedBySubUnitType.get(0).getId());
        assertEquals(child4.getId(), sortedBySubUnitType.get(1).getId());
        assertEquals(child3.getId(), sortedBySubUnitType.get(2).getId());
        assertEquals(child2.getId(), sortedBySubUnitType.get(3).getId());
        assertEquals(child1.getId(), sortedBySubUnitType.get(4).getId());

        // sort by sheetName should be child 3, 1, 5, 4, 2
        List<Work> sortedBySheetName = wcq.getChildRangeSorted(parent.getId(), 0, 10, "sheetName", true);
        assertEquals(child3.getId(), sortedBySheetName.get(0).getId());
        assertEquals(child1.getId(), sortedBySheetName.get(1).getId());
        assertEquals(child5.getId(), sortedBySheetName.get(2).getId());
        assertEquals(child4.getId(), sortedBySheetName.get(3).getId());
        assertEquals(child2.getId(), sortedBySheetName.get(4).getId());
    }

    @Test
    public void testNullShouldAlwaysBeLastInGetChildRangeSorted() throws Exception {
        // create a test work with children with properties to sort by
        Work parent = amberSession.addWork();

        // add children with properties that are not in the same sort order as the Order property or each other
        Work child1 = amberSession.addWork();
        child1.setOrder(1);
        child1.setSheetName("sheetName5");
        parent.addChild(child1);

        Work child2 = amberSession.addWork();
        child2.setOrder(2);
        child2.setSheetName("sheetName4");
        parent.addChild(child2);

        Work child3 = amberSession.addWork();
        child3.setOrder(3);
        child3.setSheetName(null);
        child3.setSubUnitType("subUnitType1");
        parent.addChild(child3);

        amberSession.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(amberSession);

        List<Work> sortedBySheetName = wcq.getChildRangeSorted(parent.getId(), 0, 10, "sheetName", true); // ascending
        assertEquals(child2.getId(), sortedBySheetName.get(0).getId());
        assertEquals(child1.getId(), sortedBySheetName.get(1).getId());
        assertEquals(child3.getId(), sortedBySheetName.get(2).getId()); // null sheetName

        sortedBySheetName = wcq.getChildRangeSorted(parent.getId(), 0, 10, "sheetName", false); // descending
        assertEquals(child1.getId(), sortedBySheetName.get(0).getId());
        assertEquals(child2.getId(), sortedBySheetName.get(1).getId());
        assertEquals(child3.getId(), sortedBySheetName.get(2).getId()); // null sheetName
    }
    
    @Test
    public void longSortByFieldsShouldBeTruncated() throws Exception {
        // create a test work with children with properties to sort by
        Work parent = amberSession.addWork();

        // add children with properties that are not in the same sort order as the Order property or each other
        Work child1 = amberSession.addWork();
        child1.setOrder(1);
        String holdingNumber = StringUtils.leftPad("", WorkChildrenQuery.TEMP_TABLE_SORT_FIELD_LENGTH + 20, "a");
        child1.setHoldingNumber(holdingNumber);
        parent.addChild(child1);

        amberSession.commit();

        WorkChildrenQuery wcq = new WorkChildrenQuery(amberSession);

        wcq.getChildRangeSorted(parent.getId(), 0, 10, "holdingNumber", true);

        try (Handle handle = new DBI(amberSrc).open()) {
            List<Map<String, Object>> tempTableContents = handle.select("select * from v1");
            assertEquals("The sort field should be truncated", WorkChildrenQuery.TEMP_TABLE_SORT_FIELD_LENGTH, ((String) tempTableContents.get(0).get("sortfield")).length());
        }
    }

    private Work buildWork() {
        Work parent = amberSession.addWork();
        
        for (int i = 0; i < 20; i++) {
            
            Page p = parent.addPage();
            p.setOrder(i);
            p.setTitle("page " + i);
            
            GeoCoding gc = p.addGeoCoding();
            gc.setGPSVersion("version " + i);
            
            Copy c = p.addCopy();
            c.setCopyRole(CopyRole.MASTER_COPY.code());
            CameraData cd = c.addCameraData();
            cd.setLens("lens");
            
            File f = c.addFile();
            f.setDevice("device " + i);
        }

        for (int i = 20; i < 30; i++) {
            
            Work w = amberSession.addWork();
            parent.addChild(w);
            w.setTitle("work " + i);
            w.setOrder(i);
            
            Copy c = w.addCopy();
            c.setCopyRole(CopyRole.ACCESS_COPY.code());
            CameraData cd = c.addCameraData();
            cd.setLens("work lens");
            
            File f = c.addFile();
            f.setDevice("work device " + i);
        }

        for (int i = 0; i < 7; i++) {
            
            Section s = parent.addSection();
            s.setTitle("section " + i);
            s.setOrder(i);

            s.addPage(parent.getPage(i+1));
        }
        
        return parent;
    }

}
