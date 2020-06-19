package eu.drus.jpa.unit.test;

import static org.junit.Assert.assertNotNull;

import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import eu.drus.jpa.unit.api.ExpectedDataSets;
import eu.drus.jpa.unit.api.InitialDataSets;
import eu.drus.jpa.unit.api.TransactionMode;
import eu.drus.jpa.unit.api.Transactional;
import eu.drus.jpa.unit.test.model.Account;
import eu.drus.jpa.unit.test.model.Depositor;
import eu.drus.jpa.unit.test.model.GiroAccount;
import eu.drus.jpa.unit.test.model.InstantAccessAccount;
import eu.drus.jpa.unit.test.model.OperationNotSupportedException;

public abstract class AbstractTransactionJunit5Test {

    @PersistenceContext(unitName = "my-test-unit")
    private EntityManager manager;

    @Test
    @InitialDataSets("datasets/initial-data.json")
    @ExpectedDataSets("datasets/initial-data.json")
    @Transactional(TransactionMode.DISABLED)
    public void transactionDisabledTest() {
        final Depositor entity = manager.find(Depositor.class, 106L);

        assertNotNull(entity);
        entity.setName("David");
    }

    @Test
    @InitialDataSets("datasets/initial-data.json")
    @ExpectedDataSets("datasets/initial-data.json")
    @Transactional(TransactionMode.ROLLBACK)
    @Disabled("It seems there is a bug in EclipseLink. If this test is executed as a first one, EclipseLink is unable to generate further IDs")
    public void transactionRollbackTest() {
        // TODO We need to wait until #13 Junit5 is implemented
        // (https://github.com/junit-team/junit5/issues/13) before we can enable this test again
        final Depositor entity = manager.find(Depositor.class, 106L);

        assertNotNull(entity);
        entity.setName("Alex");
    }

    @Test
    @InitialDataSets("datasets/initial-data.json")
    @ExpectedDataSets("datasets/expected-data.json")
    @Transactional(TransactionMode.COMMIT)
    public void transactionCommitTest() throws OperationNotSupportedException {
        final Depositor entity = manager.find(Depositor.class, 106L);

        assertNotNull(entity);
        entity.setName("Max");

        final Set<Account> accounts = entity.getAccounts();

        final GiroAccount giroAccount = accounts.stream().filter(a -> a instanceof GiroAccount).map(a -> (GiroAccount) a).findFirst().get();
        final InstantAccessAccount accessAcount = accounts.stream().filter(a -> a instanceof InstantAccessAccount)
                .map(a -> (InstantAccessAccount) a).findFirst().get();

        giroAccount.deposit(100.0f);
        giroAccount.transfer(150.0f, accessAcount);
    }
}
