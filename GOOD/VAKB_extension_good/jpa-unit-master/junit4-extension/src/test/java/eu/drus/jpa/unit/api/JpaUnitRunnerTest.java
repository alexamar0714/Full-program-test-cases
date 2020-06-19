package eu.drus.jpa.unit.api;

import static eu.drus.jpa.unit.api.TestCodeUtils.buildModel;
import static eu.drus.jpa.unit.api.TestCodeUtils.compileModel;
import static eu.drus.jpa.unit.api.TestCodeUtils.loadClass;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceProperty;
import javax.persistence.PersistenceUnit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.Description;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.mockito.ArgumentCaptor;

import com.sun.codemodel.JAnnotationArrayMember;
import com.sun.codemodel.JAnnotationUse;
import com.sun.codemodel.JCodeModel;
import com.sun.codemodel.JDefinedClass;
import com.sun.codemodel.JFieldVar;
import com.sun.codemodel.JMethod;
import com.sun.codemodel.JMod;
import com.sun.codemodel.JPackage;

public class JpaUnitRunnerTest {

    @Rule
    public TemporaryFolder testFolder = new TemporaryFolder();

    @Test
    public void testClassWithoutPersistenceContextField() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());
        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(), containsString("EntityManagerFactory or EntityManager field annotated"));
    }

    @Test
    public void testClassWithMultiplePersistenceContextFields() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar em1Field = jClass.field(JMod.PRIVATE, EntityManager.class, "em1");
        em1Field.annotate(PersistenceContext.class);
        final JFieldVar em2Field = jClass.field(JMod.PRIVATE, EntityManager.class, "em2");
        em2Field.annotate(PersistenceContext.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(), containsString("Only single field is allowed"));
    }

    @Test
    public void testClassWithMultiplePersistenceUnitFields() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emf1Field = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf1");
        emf1Field.annotate(PersistenceUnit.class);
        final JFieldVar emf2Field = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf2");
        emf2Field.annotate(PersistenceUnit.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(), containsString("Only single field is allowed"));
    }

    @Test
    public void testClassWithPersistenceContextAndPersistenceUnitFields() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emf1Field = jClass.field(JMod.PRIVATE, EntityManager.class, "em");
        emf1Field.annotate(PersistenceContext.class);
        final JFieldVar emf2Field = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf");
        emf2Field.annotate(PersistenceUnit.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(), containsString("either @PersistenceUnit or @PersistenceContext"));
    }

    @Test
    public void testClassWithPersistenceContextFieldOfWrongType() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "em");
        emField.annotate(PersistenceContext.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(), containsString("annotated with @PersistenceContext is not of type EntityManager"));
    }

    @Test
    public void testClassWithPersistenceUnitFieldOfWrongType() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManager.class, "emf");
        emField.annotate(PersistenceUnit.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(IllegalArgumentException.class));
        assertThat(failure.getException().getMessage(),
                containsString("annotated with @PersistenceUnit is not of type EntityManagerFactory"));
    }

    @Test
    public void testClassWithPersistenceContextWithoutUnitNameSpecified() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManager.class, "em");
        emField.annotate(PersistenceContext.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(JpaUnitException.class));
        assertThat(failure.getException().getMessage(), containsString("No Persistence"));
    }

    @Test
    public void testClassWithPersistenceUnitWithoutUnitNameSpecified() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf");
        emField.annotate(PersistenceUnit.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Failure> failureCaptor = ArgumentCaptor.forClass(Failure.class);
        verify(listener).testFailure(failureCaptor.capture());

        final Failure failure = failureCaptor.getValue();
        assertThat(failure.getException().getClass(), equalTo(JpaUnitException.class));
        assertThat(failure.getException().getMessage(), containsString("No Persistence"));
    }

    @Test
    public void testClassWithPersistenceContextWithKonfiguredUnitNameSpecified() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManager.class, "em");
        final JAnnotationUse jAnnotation = emField.annotate(PersistenceContext.class);
        jAnnotation.param("unitName", "test-unit-1");
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());
        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Description> descriptionCaptor = ArgumentCaptor.forClass(Description.class);
        verify(listener).testStarted(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));

        verify(listener).testFinished(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));
    }

    @Test
    public void testClassWithPersistenceUnitWithKonfiguredUnitNameSpecified() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf");
        final JAnnotationUse jAnnotation = emField.annotate(PersistenceUnit.class);
        jAnnotation.param("unitName", "test-unit-1");
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());
        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Description> descriptionCaptor = ArgumentCaptor.forClass(Description.class);
        verify(listener).testStarted(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));

        verify(listener).testFinished(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));
    }

    @Test
    public void testClassWithPersistenceContextWithWithOverwrittenConfiguration() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManager.class, "em");
        final JAnnotationUse jAnnotation = emField.annotate(PersistenceContext.class);
        jAnnotation.param("unitName", "test-unit-1");
        final JAnnotationArrayMember propArray = jAnnotation.paramArray("properties");
        propArray.annotate(PersistenceProperty.class).param("name", "javax.persistence.jdbc.url").param("value",
                "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1");
        propArray.annotate(PersistenceProperty.class).param("name", "javax.persistence.jdbc.driver").param("value", "org.h2.Driver");
        propArray.annotate(PersistenceProperty.class).param("name", "javax.persistence.jdbc.password").param("value", "test");
        propArray.annotate(PersistenceProperty.class).param("name", "javax.persistence.jdbc.user").param("value", "test");
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());
        final JpaUnitRunner runner = new JpaUnitRunner(cut);

        final RunListener listener = mock(RunListener.class);
        final RunNotifier notifier = new RunNotifier();
        notifier.addListener(listener);

        // WHEN
        runner.run(notifier);

        // THEN
        final ArgumentCaptor<Description> descriptionCaptor = ArgumentCaptor.forClass(Description.class);
        verify(listener).testStarted(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));

        verify(listener).testFinished(descriptionCaptor.capture());
        assertThat(descriptionCaptor.getValue().getClassName(), equalTo("ClassUnderTest"));
        assertThat(descriptionCaptor.getValue().getMethodName(), equalTo("testMethod"));
    }

    @Test
    public void testJpaUnitRunnerAndJpaUnitRuleFieldExcludeEachOther() throws Exception {
        // GIVEN
        final JCodeModel jCodeModel = new JCodeModel();
        final JPackage jp = jCodeModel.rootPackage();
        final JDefinedClass jClass = jp._class(JMod.PUBLIC, "ClassUnderTest");
        final JAnnotationUse jAnnotationUse = jClass.annotate(RunWith.class);
        jAnnotationUse.param("value", JpaUnitRunner.class);
        final JFieldVar emField = jClass.field(JMod.PRIVATE, EntityManagerFactory.class, "emf");
        final JAnnotationUse jAnnotation = emField.annotate(PersistenceUnit.class);
        jAnnotation.param("unitName", "test-unit-1");
        final JFieldVar ruleField = jClass.field(JMod.PUBLIC, JpaUnitRule.class, "rule");
        ruleField.annotate(Rule.class);
        final JMethod jMethod = jClass.method(JMod.PUBLIC, jCodeModel.VOID, "testMethod");
        jMethod.annotate(Test.class);

        buildModel(testFolder.getRoot(), jCodeModel);
        compileModel(testFolder.getRoot());

        final Class<?> cut = loadClass(testFolder.getRoot(), jClass.name());

        try {
            // WHEN
            new JpaUnitRunner(cut);
            fail("InitializationError expected");
        } catch (final InitializationError e) {
            // expected
            assertThat(e.getCauses().get(0).getMessage(), containsString("exclude each other"));
        }

    }
}
