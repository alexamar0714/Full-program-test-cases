package amberdb.model;

import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.tinkerpop.blueprints.Direction;
import com.tinkerpop.blueprints.Edge;

import amberdb.AbstractDatabaseIntegrationTest;
import amberdb.AmberSession;
import amberdb.relation.Acknowledge;

public class WorkAcknowledgementTest extends AbstractDatabaseIntegrationTest {

    private Party party;

    @Test
    public void shouldAddAnAcknowledgement() throws Exception {
        final String type = "of material";
        final String kindOfSupport = "lender";
        final Double weighting = 1.0;
        final Date date = new Date();
        final String url = "http://www.007.com/";

        party = amberSession.addParty("James Bond");

        Work w = amberSession.addWork();
        Acknowledge a = w.addAcknowledgement(party, type, kindOfSupport, weighting, date, url);
        amberSession.commit();

        try (AmberSession sess2 = amberDb.begin()) {
            Work w2 = sess2.findWork(w.getId());

            for (Acknowledge ack : w2.getAcknowledgements()) {
                assertNotNull(ack);
                assertEquals(ack.getAckType(), type);
                assertEquals(ack.getKindOfSupport(), kindOfSupport);
                assertEquals(ack.getWeighting(), weighting);
                assertTrue(Math.abs(ack.getDate().getTime() - date.getTime()) < 1000);
                assertEquals(ack.getUrlToOriginal(), url);
            }
        }
    }

    @Test
    public void shouldAddMultipleAcknowledgementToSameParty() {
        final String type = "of arrangement & description";
        final String kindOfSupport = "lender";
        final Double weighting = 1.1;
        final Date date = new Date();
        final String url = "http://www.web.com/";

        final String type1 = "of digitisation";
        final String kindOfSupport1 = "sponsor";
        final Double weighting1 = 2.0;
        final Date date1 = new Date();
        final String url1 = "http://www.abc.com/";

        party = amberSession.addParty("James Bond");

        Work w = amberSession.addWork();
        Acknowledge ack = w.addAcknowledgement(party, type, kindOfSupport, weighting, date, url);
        assertNotNull(ack);
        assertEquals(ack.getAckType(), type);
        assertEquals(ack.getKindOfSupport(), kindOfSupport);
        assertEquals(ack.getWeighting(), weighting);
        assertEquals(ack.getDate(), date);
        assertEquals(ack.getUrlToOriginal(), url);

        Acknowledge ack1 = w.addAcknowledgement(party, type1, kindOfSupport1, weighting1, date1, url1);
        assertNotNull(ack1);
        assertEquals(ack1.getAckType(), type1);
        assertEquals(ack1.getKindOfSupport(), kindOfSupport1);
        assertEquals(ack1.getWeighting(), weighting1);
        assertEquals(ack1.getDate(), date1);
        assertEquals(ack1.getUrlToOriginal(), url1);

        // check edge id and directions
        Edge edge = ack.asEdge();
        Edge edge1 = ack1.asEdge();
        assertEquals(edge.getVertex(Direction.IN), edge1.getVertex(Direction.IN));
        assertEquals(edge.getVertex(Direction.OUT), edge1.getVertex(Direction.OUT));
        assertThat(edge.getId(), not(edge1.getId()));
    }

    @Test
    public void shouldMapSamePartyToDifferentWork() {
        final String type1 = "of arrangement & description";
        final String kindOfSupport1 = "lender";
        final Double weighting1 = 1.1;
        final Date date1 = new Date();
        final String url1 = "http://www.web.com/";

        final String type2 = "of digitisation";
        final String kindOfSupport2 = "sponsor";
        final Double weighting2 = 2.0;
        final Date date2 = new Date();
        final String url2 = "http://www.abc.com/";

        party = amberSession.addParty("James Bond");

        Work work1 = amberSession.addWork();
        Work work2 = amberSession.addWork();
        Acknowledge ack1 = work1.addAcknowledgement(party, type1, kindOfSupport1, weighting1, date1, url1);
        Acknowledge ack2 = work2.addAcknowledgement(party, type2, kindOfSupport2, weighting2, date2, url2);

        Edge edge1 = ack1.asEdge();
        Edge edge2 = ack2.asEdge();

        assertThat(ack1.asEdge().getId(), not(ack2.asEdge().getId()));
        assertEquals(edge1.getVertex(Direction.IN), edge2.getVertex(Direction.IN));
        assertThat(edge1.getVertex(Direction.OUT), not(edge2.getVertex(Direction.OUT)));
    }

    @Test
    public void shouldAdd12AcknowledgementsToAWork() {
        Work work = amberSession.addWork();
        final String type = "of arrangement & description";
        final String kindOfSupport = "lender";
        final Date date = new Date();
        final String url = "http://www.web.com/";
        final int count = 12;

        Set<Party> parties = new HashSet<>(count);

        for (int i = 1; i < count + 1; i++) {
            Party party = amberSession.addParty("Party_" + i);
            parties.add(party);
            work.addAcknowledgement(party, type, kindOfSupport, new Double(i), date, url);
        }

        assertEquals(parties.size(), count);
        assertEquals(Iterables.size(work.getAcknowledgements()), count);

        for (Acknowledge ack : work.getAcknowledgements()) {
            parties.remove(ack.getParty());
        }

        assertEquals(parties.size(), 0);
    }

    @Test
    public void shouldRemoveOneAcknowledgementFromAWork() {

        party = amberSession.addParty("James Bond");

        Work work = amberSession.addWork();
        work.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        assertEquals(Iterables.size(work.getAcknowledgements()), 1);
        work.removeAcknowledgement(work.getAcknowledgements().iterator().next());
        assertEquals(Iterables.size(work.getAcknowledgements()), 0);
    }

    @Test
    public void shouldRemoveMultipleAcknowledgementsFromAWork() {

        party = amberSession.addParty("James Bond");

        Work work = amberSession.addWork();
        Acknowledge ack1 = work.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        Acknowledge ack2 = work.addAcknowledgement(party, "of creation of finding aids", "sponsor", 1.2, new Date(), "http://www.nla.gov.au/");
        Party party2 = amberSession.addParty("Shrek", "https://en.wikipedia.org/wiki/Shrek", "shrek's logo");
        Acknowledge ack3 = work.addAcknowledgement(party2, "of donation of digitised copy", "donor", 2.0, new Date(), "http://ourweb.nla.gov.au");
        assertEquals(Iterables.size(work.getAcknowledgements()), 3);
        work.removeAcknowledgement(ack2);
        work.removeAcknowledgement(ack3);
        assertEquals(Iterables.size(work.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work.getAcknowledgements(), ack1));
        assertFalse(Iterables.contains(work.getAcknowledgements(), ack2));
        assertFalse(Iterables.contains(work.getAcknowledgements(), ack3));
    }

    @Test
    public void shouldRemoveOnlyOneAcknowledgement() {

        party = amberSession.addParty("James Bond");

        Work work1 = amberSession.addWork();
        Work work2 = amberSession.addWork();
        Acknowledge ack1 = work1.addAcknowledgement(party, "of arrangement & description", "lender", 1.0, new Date(), "http://www.web.com/");
        Acknowledge ack2 = work2.addAcknowledgement(party, "of creation of finding aids", "sponsor", 1.2, new Date(), "http://www.nla.gov.au/");

        assertEquals(Iterables.size(work1.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work1.getAcknowledgements(), ack1));
        assertEquals(Iterables.size(work2.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work2.getAcknowledgements(), ack2));

        work2.removeAcknowledgement(ack2);

        assertEquals(Iterables.size(work1.getAcknowledgements()), 1);
        assertTrue(Iterables.contains(work1.getAcknowledgements(), ack1));
        assertEquals(Iterables.size(work2.getAcknowledgements()), 0);
        assertFalse(Iterables.contains(work2.getAcknowledgements(), ack2));
    }

    @Test
    public void shouldSortAcknowledgementsByWeighting() {

        party = amberSession.addParty("James Bond");

        Work work1 = amberSession.addWork();
        Acknowledge ack2 = work1.addAcknowledgement(party, "of arrangement & description", "lender", 2.0, new Date(), "http://www.web.com/");
        Acknowledge ack1 = work1.addAcknowledgement(party, "of creation of finding aids", "sponsor", 1.5, new Date(), "http://www.nla.gov.au/");
        Acknowledge ack0 = work1.addAcknowledgement(party, "of donation of digitised copy", "donor", 1.0, new Date(), "http://www.nla.gov.au/");
        List<Acknowledge> ackList = Lists.newArrayList(work1.getOrderedAcknowledgements());
        assertEquals(3, Iterables.size(ackList));
        assertEquals(ackList.get(0), ack0);
        assertEquals(ackList.get(1), ack1);
        assertEquals(ackList.get(2), ack2);
    }

}
