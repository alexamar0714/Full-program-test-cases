package eu.drus.jpa.unit.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import eu.drus.jpa.unit.api.Cleanup;
import eu.drus.jpa.unit.api.CleanupPhase;
import eu.drus.jpa.unit.api.CleanupUsingScripts;
import eu.drus.jpa.unit.test.model.Address;
import eu.drus.jpa.unit.test.model.ContactDetail;
import eu.drus.jpa.unit.test.model.ContactType;
import eu.drus.jpa.unit.test.model.Depositor;
import eu.drus.jpa.unit.test.model.GiroAccount;
import eu.drus.jpa.unit.test.model.OperationNotSupportedException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Cleanup(phase = CleanupPhase.NONE)
public abstract class AbstractCleanupUsingScriptTest {

    @PersistenceContext(unitName = "my-test-unit")
    private EntityManager manager;

    @Test
    public void test1() throws OperationNotSupportedException {
        // just seed the DB with some data
        final Depositor depositor = new Depositor("Max", "Payne");
        depositor.addAddress(new Address("Unknown", "111111", "Unknown", "Unknown"));
        depositor.addContactDetail(new ContactDetail(ContactType.EMAIL, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="bbd6dac3fbcbdac2d5de95d8d4d6">[email protected]</a>"));
        final GiroAccount account = new GiroAccount(depositor);
        account.deposit(100000.0f);

        // by default this test is executed in a transaction which is committed on test return. Thus
        // this entity becomes available for further tests thanks to disabled cleanup
        manager.persist(depositor);
    }

    @Test
    @CleanupUsingScripts(phase = CleanupPhase.AFTER, value = "scripts/delete-all.script")
    public void test2() {
        // since clean up is disabled we can work with the entity persisted by the previous test
        final TypedQuery<Depositor> query = manager.createQuery("SELECT d FROM Depositor d WHERE d.name='Max'", Depositor.class);
        final Depositor entity = query.getSingleResult();

        assertNotNull(entity);
    }

    @Test
    public void test3() throws OperationNotSupportedException {
        // since the entire DB is erased after the execution of the previous test, the query should
        // return an empty result set
        final TypedQuery<Depositor> query = manager.createQuery("SELECT d FROM Depositor d WHERE d.name='Max'", Depositor.class);

        assertTrue(query.getResultList().isEmpty());

        // just seed the DB with some data
        final Depositor depositor = new Depositor("Max", "Payne");
        depositor.addAddress(new Address("Unknown", "111111", "Unknown", "Unknown"));
        depositor.addContactDetail(new ContactDetail(ContactType.EMAIL, "<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="6409051c2414051d0a014a070b09">[email protected]</a>"));
        final GiroAccount account = new GiroAccount(depositor);
        account.deposit(100000.0f);

        // by default this test is executed in a transaction which is committed on test return. Thus
        // this entity becomes available for further tests thanks to disabled cleanup
        manager.persist(depositor);
    }

    @Test
    public void test4() {
        // since clean up is disabled we can work with the entity persisted by the previous test
        final TypedQuery<Depositor> query = manager.createQuery("SELECT d FROM Depositor d WHERE d.name='Max'", Depositor.class);
        final Depositor entity = query.getSingleResult();

        assertNotNull(entity);
    }

    @Test
    @CleanupUsingScripts(phase = CleanupPhase.BEFORE, value = "scripts/delete-all.script")
    public void test5() {
        // since the entire DB is erased before the execution of the given test, the query should
        // return an empty result set
        final TypedQuery<Depositor> query = manager.createQuery("SELECT d FROM Depositor d WHERE d.name='Max'", Depositor.class);

        assertTrue(query.getResultList().isEmpty());
    }
}
