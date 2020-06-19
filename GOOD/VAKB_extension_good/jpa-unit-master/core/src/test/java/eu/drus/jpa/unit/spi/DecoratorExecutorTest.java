package eu.drus.jpa.unit.spi;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import eu.drus.jpa.unit.core.DecoratorRegistrar;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore("org.mockito.*")
@PrepareForTest(DecoratorRegistrar.class)
public class DecoratorExecutorTest {

    @Mock
    private TestClassDecorator firstClassDecorator;

    @Mock
    private TestClassDecorator secondClassDecorator;

    @Mock
    private TestMethodDecorator firstMethodDecorator;

    @Mock
    private TestMethodDecorator secondMethodDecorator;

    @Mock
    private TestInvocation invocation;

    @Mock
    private ExecutionContext jpaUnitContext;

    @SuppressWarnings({
            "unchecked", "rawtypes"
    })
    @Before
    public void prepareMocks() throws Exception {
        mockStatic(DecoratorRegistrar.class);

        when(DecoratorRegistrar.getClassDecorators()).thenReturn(Arrays.asList(firstClassDecorator, secondClassDecorator));
        when(DecoratorRegistrar.getMethodDecorators()).thenReturn(Arrays.asList(firstMethodDecorator, secondMethodDecorator));

        when(firstClassDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(secondClassDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(firstMethodDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);
        when(secondMethodDecorator.isConfigurationSupported(any(ExecutionContext.class))).thenReturn(Boolean.TRUE);

        when(firstClassDecorator.getPriority()).thenReturn(1);
        when(secondClassDecorator.getPriority()).thenReturn(2);

        when(firstMethodDecorator.getPriority()).thenReturn(1);
        when(secondMethodDecorator.getPriority()).thenReturn(2);

        when(invocation.getContext()).thenReturn(jpaUnitContext);
        when(invocation.getTestClass()).thenReturn((Class) getClass());
    }

    @Test
    public void testProcessBeforeAllTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final DecoratorExecutor unit = new DecoratorExecutor();

        // WHEN
        unit.processBeforeAll(invocation);

        // THEN
        final InOrder order = inOrder(firstClassDecorator, secondClassDecorator);
        order.verify(firstClassDecorator).beforeAll(eq(invocation));
        order.verify(secondClassDecorator).beforeAll(eq(invocation));
        verifyZeroInteractions(firstMethodDecorator, secondMethodDecorator);
    }

    @Test
    public void testProcessAfterAllTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final DecoratorExecutor unit = new DecoratorExecutor();

        // WHEN
        unit.processAfterAll(invocation);

        // THEN
        final InOrder order = inOrder(secondClassDecorator, firstClassDecorator);
        order.verify(secondClassDecorator).afterAll(eq(invocation));
        order.verify(firstClassDecorator).afterAll(eq(invocation));
        verifyZeroInteractions(firstMethodDecorator, secondMethodDecorator);
    }

    @Test
    public void testProcessBeforeTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final DecoratorExecutor unit = new DecoratorExecutor();

        // WHEN
        unit.processBefore(invocation);

        // THEN
        final InOrder order = inOrder(firstMethodDecorator, secondMethodDecorator);
        order.verify(firstMethodDecorator).beforeTest(eq(invocation));
        order.verify(secondMethodDecorator).beforeTest(eq(invocation));
        verifyZeroInteractions(firstClassDecorator, secondClassDecorator);
    }

    @Test
    public void testProcessAfterTestDecoratorExecutionOrder() throws Exception {
        // GIVEN
        final DecoratorExecutor unit = new DecoratorExecutor();

        // WHEN
        unit.processAfter(invocation);

        // THEN
        final InOrder order = inOrder(firstMethodDecorator, secondMethodDecorator);
        order.verify(secondMethodDecorator).afterTest(eq(invocation));
        order.verify(firstMethodDecorator).afterTest(eq(invocation));
        verifyZeroInteractions(firstClassDecorator, secondClassDecorator);
    }
}
