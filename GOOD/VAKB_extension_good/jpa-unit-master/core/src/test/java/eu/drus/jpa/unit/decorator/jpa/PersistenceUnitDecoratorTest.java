package eu.drus.jpa.unit.decorator.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Optional;

import javax.persistence.EntityManagerFactory;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.drus.jpa.unit.spi.Constants;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.TestInvocation;

@RunWith(MockitoJUnitRunner.class)
public class PersistenceUnitDecoratorTest {

    @Mock
    private TestInvocation invocation;

    @Mock
    private ExecutionContext ctx;

    @Mock
    private EntityManagerFactory entityManagerFactory;

    private EntityManagerFactory emf;

    @SuppressWarnings("unused")
    private Object someField;

    @Before
    public void setupMocks() {
        when(invocation.getContext()).thenReturn(ctx);
        when(invocation.getTestInstance()).thenReturn(Optional.of(this));
        when(ctx.getData(eq(Constants.KEY_ENTITY_MANAGER_FACTORY))).thenReturn(entityManagerFactory);
    }

    @Test
    public void testApplyEntityManagerFactoryInjection() throws Throwable {
        // GIVEN
        final Field field = getClass().getDeclaredField("emf");
        when(ctx.getPersistenceField()).thenReturn(field);
        final PersistenceUnitDecorator fixture = new PersistenceUnitDecorator();

        // WHEN
        fixture.beforeTest(invocation);

        // THEN
        assertThat(emf, equalTo(entityManagerFactory));
        verify(entityManagerFactory, times(0)).createEntityManager();
        verify(entityManagerFactory, times(0)).close();
    }

    @Test
    public void testApplyForAnUnsupportedField() throws Throwable {
        // GIVEN
        final Field field = getClass().getDeclaredField("someField");
        when(ctx.getPersistenceField()).thenReturn(field);
        final PersistenceUnitDecorator fixture = new PersistenceUnitDecorator();

        // WHEN
        fixture.beforeTest(invocation);

        // THEN
        assertThat(emf, nullValue());
        verify(entityManagerFactory, times(0)).createEntityManager();
        verify(entityManagerFactory, times(0)).close();
    }

    @Test
    public void testAfterTestDoesNotHaveAnyEffect() throws Exception {
        // GIVEN
        final PersistenceUnitDecorator fixture = new PersistenceUnitDecorator();

        // WHEN
        fixture.afterTest(invocation);

        // THEN
        verifyNoMoreInteractions(entityManagerFactory, ctx, invocation);
    }

    @Test
    public void testRequiredPriority() {
        // GIVEN
        final PersistenceUnitDecorator fixture = new PersistenceUnitDecorator();

        // WHEN
        final int priority = fixture.getPriority();

        // THEN
        assertThat(priority, equalTo(1));
    }
}
