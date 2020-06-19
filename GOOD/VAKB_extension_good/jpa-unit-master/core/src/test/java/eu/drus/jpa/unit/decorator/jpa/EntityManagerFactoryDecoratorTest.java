package eu.drus.jpa.unit.decorator.jpa;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.HashMap;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.drus.jpa.unit.spi.Constants;
import eu.drus.jpa.unit.spi.ExecutionContext;
import eu.drus.jpa.unit.spi.PersistenceUnitDescriptor;
import eu.drus.jpa.unit.spi.TestInvocation;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Persistence.class)
public class EntityManagerFactoryDecoratorTest {

    private static final HashMap<String, Object> PERSISTENCE_PROPERTIES = new HashMap<>();

    private static final String UNIT_NAME = "MY_UNIT_NAME";

    @Mock
    private ExecutionContext ctx;

    @Mock
    private TestInvocation invocation;

    @Mock
    private EntityManagerFactory factory;

    @Mock
    private PersistenceUnitDescriptor descriptor;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Before
    public void prepareMocks() {
        mockStatic(Persistence.class);
        when(Persistence.createEntityManagerFactory(eq(UNIT_NAME), eq(PERSISTENCE_PROPERTIES))).thenReturn(factory);

        when(invocation.getContext()).thenReturn(ctx);
        when(invocation.getTestClass()).thenReturn((Class) getClass());
        when(ctx.getDescriptor()).thenReturn(descriptor);
        when(descriptor.getUnitName()).thenReturn(UNIT_NAME);
        when(descriptor.getProperties()).thenReturn(PERSISTENCE_PROPERTIES);
    }

    @Test
    public void testRequiredPriority() {
        // GIVEN
        final EntityManagerFactoryDecorator decorator = new EntityManagerFactoryDecorator();

        // WHEN
        final int priority = decorator.getPriority();

        // THEN
        assertThat(priority, equalTo(2));
    }

    @Test
    public void testBeforeAll() throws Throwable {
        // GIVEN
        final EntityManagerFactoryDecorator decorator = new EntityManagerFactoryDecorator();

        // WHEN
        decorator.beforeAll(invocation);

        // THEN
        verify(ctx).storeData(eq(Constants.KEY_ENTITY_MANAGER_FACTORY), eq(factory));
    }

    @Test
    public void testAfterAll() throws Throwable {
        // GIVEN
        when(ctx.getData(Constants.KEY_ENTITY_MANAGER_FACTORY)).thenReturn(factory);

        final EntityManagerFactoryDecorator decorator = new EntityManagerFactoryDecorator();

        // WHEN
        decorator.afterAll(invocation);

        // THEN
        verify(ctx).storeData(eq(Constants.KEY_ENTITY_MANAGER_FACTORY), eq(null));
        verify(factory).close();
    }

}
