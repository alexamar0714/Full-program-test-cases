//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.application.arraydata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import gov.nih.nci.caarray.application.AbstractServiceTest;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignService;
import gov.nih.nci.caarray.application.arraydesign.ArrayDesignServiceBean;
import gov.nih.nci.caarray.application.fileaccess.FileAccessService;
import gov.nih.nci.caarray.application.fileaccess.FileAccessServiceStub;
import gov.nih.nci.caarray.application.vocabulary.VocabularyService;
import gov.nih.nci.caarray.application.vocabulary.VocabularyServiceStub;
import gov.nih.nci.caarray.dao.ArrayDao;
import gov.nih.nci.caarray.dao.ContactDao;
import gov.nih.nci.caarray.dao.MultipartBlobDao;
import gov.nih.nci.caarray.dao.SearchDao;
import gov.nih.nci.caarray.dao.VocabularyDao;
import gov.nih.nci.caarray.dao.stub.ArrayDaoStub;
import gov.nih.nci.caarray.dao.stub.ContactDaoStub;
import gov.nih.nci.caarray.dao.stub.DaoFactoryStub;
import gov.nih.nci.caarray.dao.stub.SearchDaoStub;
import gov.nih.nci.caarray.dao.stub.VocabularyDaoStub;
import gov.nih.nci.caarray.dataStorage.DataStorageModule;
import gov.nih.nci.caarray.dataStorage.fileSystem.FileSystemStorageModule;
import gov.nih.nci.caarray.dataStorage.spi.DataStorage;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.data.AbstractArrayData;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.DesignElementList;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.file.FileType;
import gov.nih.nci.caarray.domain.project.Experiment;
import gov.nih.nci.caarray.domain.project.Project;
import gov.nih.nci.caarray.platforms.PlatformModule;
import gov.nih.nci.caarray.platforms.SessionTransactionManager;
import gov.nih.nci.caarray.platforms.SessionTransactionManagerNoOpImpl;
import gov.nih.nci.caarray.staticinjection.CaArrayEjbStaticInjectionModule;
import gov.nih.nci.caarray.test.data.arraydesign.AffymetrixArrayDesignFiles;
import gov.nih.nci.caarray.util.CaArrayHibernateHelperModule;
import gov.nih.nci.caarray.util.j2ee.ServiceLocatorStub;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.fiveamsolutions.nci.commons.data.persistent.PersistentObject;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.name.Names;

/**
 * Tests the ArrayDataService subsystem
 */
@SuppressWarnings("PMD")
public abstract class AbstractArrayDataServiceTest extends AbstractServiceTest {
    protected static final String AFFY_TEST3_LSID_OBJECT_ID = "Test3";
    protected static final String HG_FOCUS_LSID_OBJECT_ID = "HG-Focus";
    protected static final String AGILENT_ACGH_LSID_OBJECT_ID = "Agilent-aCGH";

    protected static final DataImportOptions DEFAULT_IMPORT_OPTIONS = DataImportOptions.getAutoCreatePerFileOptions();

    public ArrayDataService arrayDataService;
    protected FileAccessServiceStub fileAccessServiceStub = new FileAccessServiceStub();
    protected LocalDaoFactoryStub daoFactoryStub = new LocalDaoFactoryStub();
    protected LocalSearchDaoStub searchDaoStub = new LocalSearchDaoStub();
    protected VocabularyServiceStub vocabularyServiceStub = new VocabularyServiceStub();

    private long fileIdCounter = 1;

    @Before
    public void setUp() throws Exception {
        final ServiceLocatorStub locatorStub = ServiceLocatorStub.registerEmptyLocator();
        locatorStub.addLookup(FileAccessService.JNDI_NAME, this.fileAccessServiceStub);
        locatorStub.addLookup(VocabularyService.JNDI_NAME, this.vocabularyServiceStub);

        final Module testModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ContactDao.class).toInstance(new ContactDaoStub());
                bind(SearchDao.class).toInstance(AbstractArrayDataServiceTest.this.daoFactoryStub.getSearchDao());
                bind(ArrayDao.class).toInstance(AbstractArrayDataServiceTest.this.daoFactoryStub.getArrayDao());
                bind(VocabularyService.class).toInstance(AbstractArrayDataServiceTest.this.vocabularyServiceStub);
                bind(VocabularyDao.class).toInstance(new VocabularyDaoStub());
                bind(MultipartBlobDao.class).toInstance(mock(MultipartBlobDao.class));

                final Multibinder<ArrayDataTypeDescriptor> arrayDataDescriptorBinder =
                        Multibinder.newSetBinder(binder(), ArrayDataTypeDescriptor.class);
                arrayDataDescriptorBinder.addBinding().toInstance(TestArrayDescriptor.INSTANCE);

                bind(ArrayDesignService.class).to(ArrayDesignServiceBean.class);
                bind(ArrayDataService.class).to(ArrayDataServiceBean.class);

                final MapBinder<String, DataStorage> mapBinder =
                        MapBinder.newMapBinder(binder(), String.class, DataStorage.class);
                mapBinder.addBinding(FileAccessServiceStub.SCHEME).toInstance(
                        AbstractArrayDataServiceTest.this.fileAccessServiceStub);

                bind(SessionTransactionManager.class).to(SessionTransactionManagerNoOpImpl.class);

                bind(String.class).annotatedWith(Names.named(FileSystemStorageModule.BASE_DIR_KEY)).toInstance(
                        System.getProperty("java.io.tmpdir"));
                bind(String.class).annotatedWith(Names.named(DataStorageModule.FILE_DATA_ENGINE)).toInstance(
                        "file-system");
                bind(String.class).annotatedWith(Names.named(DataStorageModule.PARSED_DATA_ENGINE)).toInstance(
                        "file-system");
            }
        };

        final PlatformModule platformModule = new PlatformModule();
        configurePlatforms(platformModule);

        final Injector injector =
                Guice.createInjector(new CaArrayEjbStaticInjectionModule(), new CaArrayHibernateHelperModule(),
                        new DataStorageModule(), platformModule, testModule);

        final ArrayDesignServiceBean arrayDesignServiceBean =
                (ArrayDesignServiceBean) injector.getInstance(ArrayDesignService.class);

        locatorStub.addLookup(ArrayDesignService.JNDI_NAME, arrayDesignServiceBean);

        final ArrayDataServiceBean arrayDataServiceBean =
                (ArrayDataServiceBean) injector.getInstance(ArrayDataService.class);

        this.arrayDataService = arrayDataServiceBean;
        this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEST3_CDF);
        this.fileAccessServiceStub.add(AffymetrixArrayDesignFiles.TEN_K_CDF);
    }

    /**
     * Strategy method allowing subclasses to add platform implementations to the configuration
     * 
     * @param platformModule
     */
    protected void configurePlatforms(PlatformModule platformModule) {
        // no-op by default
    }

    protected void checkAnnotation(CaArrayFile dataFile, int numberOfSamples) {
        final Experiment experiment = dataFile.getProject().getExperiment();
        assertEquals(numberOfSamples, experiment.getSources().size());
        assertEquals(numberOfSamples, experiment.getSamples().size());
        assertEquals(numberOfSamples, experiment.getExtracts().size());
        assertEquals(numberOfSamples, experiment.getLabeledExtracts().size());
    }

    @SuppressWarnings("deprecation")
    public CaArrayFile getDataCaArrayFile(File file, FileType type) {
        final CaArrayFile caArrayFile = this.fileAccessServiceStub.add(file);
        caArrayFile.setId(this.fileIdCounter++);
        caArrayFile.setFileType(type);
        caArrayFile.setProject(new Project());
        caArrayFile.getProject().setExperiment(new Experiment());
        return caArrayFile;
    }

    /**
     * Subclasses should override to create array designs as needed
     * 
     * @param lsidAuthority
     * @param lsidNamespace
     * @param lsidObjectId
     * @return
     */
    protected ArrayDesign createArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
        final ArrayDesign arrayDesign = new ArrayDesign();
        final CaArrayFile f = new CaArrayFile();
        f.setFileStatus(FileStatus.IMPORTED);
        arrayDesign.getDesignFiles().add(f);
        return arrayDesign;
    }

    protected final class LocalSearchDaoStub extends SearchDaoStub {
        private final Map<Long, PersistentObject> objMap = new HashMap<Long, PersistentObject>();

        @Override
        public Long save(PersistentObject caArrayEntity) {
            super.save(caArrayEntity);
            this.objMap.put(caArrayEntity.getId(), caArrayEntity);
            return caArrayEntity.getId();
        }

        @SuppressWarnings("unchecked")
        @Override
        public <T extends PersistentObject> T retrieve(Class<T> entityClass, Long entityId) {
            final Object candidate = this.objMap.get(entityId);
            if (candidate == null) {
                return null;
            } else {
                return (T) (entityClass.isInstance(candidate) ? candidate : null);
            }
        }
    }

    public final class LocalDaoFactoryStub extends DaoFactoryStub {

        protected final Map<ArrayDataTypeDescriptor, ArrayDataType> dataTypeMap =
                new HashMap<ArrayDataTypeDescriptor, ArrayDataType>();

        protected final Map<QuantitationTypeDescriptor, QuantitationType> quantitationTypeMap =
                new HashMap<QuantitationTypeDescriptor, QuantitationType>();

        protected final Map<Long, AbstractArrayData> fileDataMap = new HashMap<Long, AbstractArrayData>();

        protected final Map<String, ArrayDesign> arrayDesignMap = new HashMap<String, ArrayDesign>();

        @Override
        public SearchDao getSearchDao() {
            return AbstractArrayDataServiceTest.this.searchDaoStub;
        }

        @Override
        public ArrayDao getArrayDao() {
            return new ArrayDaoStub() {

                @Override
                public ArrayDesign getArrayDesign(String lsidAuthority, String lsidNamespace, String lsidObjectId) {
                    if (LocalDaoFactoryStub.this.arrayDesignMap.containsKey(lsidObjectId)) {
                        return LocalDaoFactoryStub.this.arrayDesignMap.get(lsidObjectId);
                    } else {
                        final ArrayDesign design = createArrayDesign(lsidAuthority, lsidNamespace, lsidObjectId);
                        LocalDaoFactoryStub.this.arrayDesignMap.put(lsidObjectId, design);
                        return design;
                    }
                }

                @Override
                public ArrayDataType getArrayDataType(ArrayDataTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.dataTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.dataTypeMap.get(descriptor);
                    }
                    final ArrayDataType arrayDataType = new ArrayDataType();
                    arrayDataType.setName(descriptor.getName());
                    arrayDataType.setVersion(descriptor.getVersion());
                    addQuantitationTypes(arrayDataType, descriptor);
                    LocalDaoFactoryStub.this.dataTypeMap.put(descriptor, arrayDataType);
                    return arrayDataType;
                }

                private void addQuantitationTypes(ArrayDataType arrayDataType, ArrayDataTypeDescriptor descriptor) {
                    for (final QuantitationTypeDescriptor quantitationTypeDescriptor : descriptor
                            .getQuantitationTypes()) {
                        arrayDataType.getQuantitationTypes().add(getQuantitationType(quantitationTypeDescriptor));
                    }
                }

                @Override
                public QuantitationType getQuantitationType(QuantitationTypeDescriptor descriptor) {
                    if (LocalDaoFactoryStub.this.quantitationTypeMap.containsKey(descriptor)) {
                        return LocalDaoFactoryStub.this.quantitationTypeMap.get(descriptor);
                    }
                    final QuantitationType quantitationType = new QuantitationType();
                    quantitationType.setName(descriptor.getName());
                    quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
                    LocalDaoFactoryStub.this.quantitationTypeMap.put(descriptor, quantitationType);
                    return quantitationType;
                }

                @Override
                public AbstractArrayData getArrayData(Long fileId) {
                    return LocalDaoFactoryStub.this.fileDataMap.get(fileId);
                }

                @Override
                public Long save(PersistentObject caArrayEntity) {
                    if (caArrayEntity instanceof AbstractArrayData) {
                        addData((AbstractArrayData) caArrayEntity);
                    }
                    return caArrayEntity.getId();
                }

                @Override
                public DesignElementList getDesignElementList(String lsidAuthority, String lsidNamespace,
                        String lsidObjectId) {
                    final DesignElementList list = new DesignElementList();
                    list.setLsidForEntity(lsidAuthority + ":" + lsidNamespace + ":" + lsidObjectId);
                    return list;
                }

            };
        }

        void addData(AbstractArrayData arrayData) {
            this.fileDataMap.put(arrayData.getDataFile().getId(), arrayData);
        }
    }

    /**
     * Descriptor for data formats that aren't supported.
     */
    protected static class TestArrayDescriptor implements ArrayDataTypeDescriptor {
        protected static ArrayDataTypeDescriptor INSTANCE = new TestArrayDescriptor();

        /**
         * {@inheritDoc}
         */
        @Override
        public String getName() {
            return "Test";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public List<QuantitationTypeDescriptor> getQuantitationTypes() {
            return Collections.singletonList(TestQuantitationDescriptor.INSTANCE);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getVersion() {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isEquivalent(ArrayDataType arrayDataType) {
            return this.equals("Name".equals(arrayDataType.getName()));
        }
    }

    protected static class TestQuantitationDescriptor implements QuantitationTypeDescriptor {
        protected static QuantitationTypeDescriptor INSTANCE = new TestQuantitationDescriptor();

        @Override
        public DataType getDataType() {
            return DataType.BOOLEAN;
        }

        @Override
        public String getName() {
            return "TestQT";
        }
    }
}
