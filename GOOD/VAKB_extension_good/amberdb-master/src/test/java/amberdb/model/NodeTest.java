package amberdb.model;

import amberdb.AmberSession;
import amberdb.AmberDb;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class NodeTest {

    private AmberSession db;

    @Before
    public void setup() throws IOException, InstantiationException {
        db = new AmberSession();
    }

    @After
    public void teardown() throws IOException {
        if (db != null)
            db.close();
    }

    @Test
    public void propertySetHasAllProperties() {
        Work w = db.addWork();
        w.setBibLevel("bib");
        assertEquals(w.getPropertyKeySet(), Sets.newHashSet("bibLevel", "type"));
    }
}
