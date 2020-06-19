package amberdb.model;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberDb;
import amberdb.AmberSession;

public class TagTest extends AbstractDatabaseIntegrationTest{
    
    @Test
    public void basicTagTests() throws Exception { 
        
        // creation
        Tag tag = amberSession.addTag();
        Tag tag2 = amberSession.addTag("tag2");

        assertNotNull(tag);
        assertNotNull(tag2);
        assertTrue(tag instanceof Tag);        
        assertTrue(tag2 instanceof Tag);
        assertEquals(tag.getName(), null);        
        assertEquals(tag2.getName(), "tag2");
        
        // tag persists for session suspend
        Long sessId = amberSession.suspend();
        AmberSession sess2 = amberDb.resume(sessId);
        Tag tag3 = sess2.findModelObjectById(tag.getId(), Tag.class);
        assertEquals(tag3, tag);
        
        // tag persists for session commit
        amberSession.commit("tester", "save tag");
        AmberSession sess3 = amberDb.begin();
        Tag tag4 = sess3.findModelObjectById(tag2.getId(), Tag.class);
        assertEquals(tag4, tag2);
        
        // tags can be searched for and found
        amberSession.addTag("t5");
        amberSession.addTag("t6");
        amberSession.addTag("t7");
        amberSession.commit("tester", "save tags for search");
        
        Iterable<Tag> tagColl1 = sess2.getAllTags();
        int i = 0;
        for (Tag t : tagColl1) {
            i++;
        }
        assertEquals(i, 5);
        
        sess2.close();
        sess3.close();
    }
    
    @Test
    public void nodeTaggingTests() throws Exception { 
        
        // creation
        Tag tag = amberSession.addTag("tag1");
        Tag tag2 = amberSession.addTag("tag2");

        assertNotNull(tag);
        assertNotNull(tag2);
        
        Work w = amberSession.addWork();
        w.addTag(tag);
        
        assertEquals(w.getTags().iterator().next(), tag);
        assertNotEquals(w.getTags().iterator().next(), tag2);
        
        w.addTag(tag2);
        
        int i = 0;
        for(Tag t : w.getTags()) {
            i++;
        }
        assertEquals(i, 2);
        
        // tag persists for session suspend
        Long sessId = amberSession.suspend();
        AmberSession sess2 = amberDb.resume(sessId);
        
        Work w1 = sess2.findWork(w.getId());
        i = 0;
        for(Tag t : w1.getTags()) {
            i++;
        }
        assertEquals(i, 2);
        
        // tag persists for session commit
        amberSession.commit("tester", "save tag");
        AmberSession sess3 = amberDb.begin();

        Work w2 = sess3.findWork(w.getId());
        
        i = 0;
        for(Tag t : w2.getTags()) {
            i++;
        }
        assertEquals(i, 2);

        // get all tags in system
        for (Tag t : sess3.getAllTags()) {
            if (t.getName().equals("tag2")) {
                i = 0;
                for (Node n : t.getTaggedObjects()) {
                    i++;
                }
                assertEquals(i, 1);
            }
        }
        
        // try removing the tags
        for(Tag t : w2.getTags()) {
            w2.removeTag(t);
        }
        i = 0;
        for(Tag t : w2.getTags()) {
            i++;
        }
        assertEquals(i, 0);
        
        
        sess2.close();
        sess3.close();
    }   
    
    private void s(Object o) {
        System.out.println(o);
    }
}

