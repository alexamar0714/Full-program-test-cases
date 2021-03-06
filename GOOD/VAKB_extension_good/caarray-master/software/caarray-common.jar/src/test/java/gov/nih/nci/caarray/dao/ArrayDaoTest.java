//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import edu.georgetown.pir.Organism;
import gov.nih.nci.caarray.domain.array.ArrayDesign;
import gov.nih.nci.caarray.domain.array.ArrayDesignDetails;
import gov.nih.nci.caarray.domain.contact.Organization;
import gov.nih.nci.caarray.domain.data.ArrayDataType;
import gov.nih.nci.caarray.domain.data.ArrayDataTypeDescriptor;
import gov.nih.nci.caarray.domain.data.DataType;
import gov.nih.nci.caarray.domain.data.QuantitationType;
import gov.nih.nci.caarray.domain.data.QuantitationTypeDescriptor;
import gov.nih.nci.caarray.domain.data.RawArrayData;
import gov.nih.nci.caarray.domain.file.CaArrayFile;
import gov.nih.nci.caarray.domain.file.FileStatus;
import gov.nih.nci.caarray.domain.project.AssayType;
import gov.nih.nci.caarray.domain.vocabulary.Category;
import gov.nih.nci.caarray.domain.vocabulary.Term;
import gov.nih.nci.caarray.domain.vocabulary.TermSource;
import gov.nih.nci.caarray.util.CaArrayUtils;

import java.net.URI;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the Array DAO.
 *
 * @author Rashmi Srinivasa
 */
@SuppressWarnings("PMD")
public class ArrayDaoTest extends AbstractDaoTest {
    private static final Logger LOG = Logger.getLogger(ArrayDaoTest.class);

    private static final URI DUMMY_HANDLE = CaArrayUtils.makeUriQuietly("foo:baz");

    private static Organization DUMMY_ORGANIZATION = new Organization();
    private static Organization DUMMY_ORGANIZATION2 = new Organization();
    private static Organization DUMMY_ORGANIZATION3 = new Organization();
    private static ArrayDesign DUMMY_ARRAYDESIGN_1 = new ArrayDesign();
    private static ArrayDesign DUMMY_ARRAYDESIGN_2 = new ArrayDesign();
    private static ArrayDesign DUMMY_ARRAYDESIGN_3 = new ArrayDesign();
    private static AssayType DUMMY_ASSAY_TYPE1;
    private static AssayType DUMMY_ASSAY_TYPE2;
    private static int uniqueInt = 0;

    private ArrayDao daoObject;

    /**
     * Define the dummy objects that will be used by the tests.
     */
    @Before
    public void setUp() {
        this.daoObject = CaArrayDaoFactory.INSTANCE.getArrayDao();

        // Initialize all the dummy objects needed for the tests.
        initializeArrayDesigns();
        Transaction tx = null;
        // Save dummy objects to database.
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(DUMMY_ARRAYDESIGN_1);
            this.daoObject.save(DUMMY_ARRAYDESIGN_2);
            this.daoObject.save(DUMMY_ARRAYDESIGN_3);
            this.daoObject.save(DUMMY_ORGANIZATION3);
            this.daoObject.save(DUMMY_ASSAY_TYPE1);
            this.daoObject.save(DUMMY_ASSAY_TYPE2);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            LOG.error("Error setting up test data.", e);
            throw e;
        }
    }

    @After
    public void cleanupObjects() {
        Transaction tx = null;
        // Save dummy objects to database.

        tx = this.hibernateHelper.beginTransaction();
        final Session s = this.hibernateHelper.getCurrentSession();
        s.delete(s.merge(DUMMY_ARRAYDESIGN_1));
        s.delete(s.merge(DUMMY_ARRAYDESIGN_2));
        s.delete(s.merge(DUMMY_ARRAYDESIGN_3));
        s.delete(s.merge(DUMMY_ORGANIZATION3));
        s.delete(s.merge(DUMMY_ASSAY_TYPE1));
        s.delete(s.merge(DUMMY_ASSAY_TYPE2));
        tx.commit();

    }

    /**
     * Initialize the dummy <code>ArrayDesign</code> objects.
     */
    private static void initializeArrayDesigns() {
        final TermSource ts = new TermSource();
        ts.setName("TS 1");
        final Category cat = new Category();
        cat.setName("catName");
        cat.setSource(ts);

        final Term term = new Term();
        term.setValue("testval");
        term.setCategory(cat);
        term.setSource(ts);

        final Organism organism = new Organism();
        organism.setScientificName("Homo sapiens");
        organism.setTermSource(ts);

        DUMMY_ORGANIZATION = new Organization();
        DUMMY_ORGANIZATION.setName("DummyOrganization");
        DUMMY_ORGANIZATION2 = new Organization();
        DUMMY_ORGANIZATION2.setName("DummyOrganization2");
        DUMMY_ORGANIZATION3 = new Organization();
        DUMMY_ORGANIZATION3.setName("DummyOrganization3");
        DUMMY_ARRAYDESIGN_1 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_1.setName("DummyTestArrayDesign1");
        DUMMY_ARRAYDESIGN_1.setVersion("2.0");
        DUMMY_ARRAYDESIGN_1.setGeoAccession("GPL0001");
        DUMMY_ARRAYDESIGN_1.setProvider(DUMMY_ORGANIZATION);
        DUMMY_ARRAYDESIGN_1.setLsidForEntity("authority:namespace:objectId");
        DUMMY_ASSAY_TYPE1 = new AssayType("Gene Expression");
        DUMMY_ASSAY_TYPE2 = new AssayType("SNP");
        SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        DUMMY_ARRAYDESIGN_1.setAssayTypes(assayTypes);
        final CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
        file.setDataHandle(DUMMY_HANDLE);
        DUMMY_ARRAYDESIGN_1.addDesignFile(file);
        DUMMY_ARRAYDESIGN_1.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_1.setOrganism(organism);
        DUMMY_ARRAYDESIGN_2 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_2.setName("DummyTestArrayDesign2");
        DUMMY_ARRAYDESIGN_2.setVersion("2.0");
        DUMMY_ARRAYDESIGN_2.setGeoAccession("GPL0002");
        DUMMY_ARRAYDESIGN_2.setProvider(DUMMY_ORGANIZATION2);
        final CaArrayFile file2 = new CaArrayFile();
        file2.setFileStatus(FileStatus.IMPORTING);
        file2.setDataHandle(DUMMY_HANDLE);
        DUMMY_ARRAYDESIGN_2.addDesignFile(file2);
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        assayTypes.add(DUMMY_ASSAY_TYPE2);
        DUMMY_ARRAYDESIGN_2.setAssayTypes(assayTypes);
        DUMMY_ARRAYDESIGN_2.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_2.setOrganism(organism);
        DUMMY_ARRAYDESIGN_3 = new ArrayDesign();
        DUMMY_ARRAYDESIGN_3.setName("DummyTestArrayDesign3");
        DUMMY_ARRAYDESIGN_3.setVersion("2.0");
        DUMMY_ARRAYDESIGN_3.setGeoAccession("GPL0003");
        DUMMY_ARRAYDESIGN_3.setProvider(DUMMY_ORGANIZATION2);
        final CaArrayFile file3 = new CaArrayFile();
        file3.setFileStatus(FileStatus.IMPORTED);
        file3.setDataHandle(DUMMY_HANDLE);
        final CaArrayFile file4 = new CaArrayFile();
        file4.setFileStatus(FileStatus.IMPORTED);
        file4.setDataHandle(DUMMY_HANDLE);
        DUMMY_ARRAYDESIGN_3.addDesignFile(file3);
        DUMMY_ARRAYDESIGN_3.addDesignFile(file4);
        assayTypes = new TreeSet<AssayType>();
        assayTypes.add(DUMMY_ASSAY_TYPE1);
        DUMMY_ARRAYDESIGN_3.setAssayTypes(assayTypes);
        DUMMY_ARRAYDESIGN_3.setTechnologyType(term);
        DUMMY_ARRAYDESIGN_3.setOrganism(organism);
    }

    @Test
    public void testGetArrayDesign() throws Exception {
        Transaction tx = null;
        this.hibernateHelper.setFiltersEnabled(false);
        try {
            tx = this.hibernateHelper.beginTransaction();
            final ArrayDesign retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            assertEquals(DUMMY_ARRAYDESIGN_1, retrievedArrayDesign);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testChangeArrayDesignFile() throws Exception {
        Transaction tx = null;
        this.hibernateHelper.setFiltersEnabled(false);
        try {
            tx = this.hibernateHelper.beginTransaction();
            ArrayDesign retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            final CaArrayFile file2 = new CaArrayFile();
            file2.setName("newfile");
            file2.setFileStatus(FileStatus.IMPORTING);
            file2.setDataHandle(DUMMY_HANDLE);
            retrievedArrayDesign.getDesignFiles().clear();
            retrievedArrayDesign.addDesignFile(file2);
            this.daoObject.save(retrievedArrayDesign);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_1.getId());
            assertEquals("newfile", retrievedArrayDesign.getFirstDesignFile().getName());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetArrayDesignByLsid() throws Exception {
        Transaction tx = null;
        this.hibernateHelper.setFiltersEnabled(false);
        try {
            tx = this.hibernateHelper.beginTransaction();
            ArrayDesign retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_1.getLsidAuthority(),
                    DUMMY_ARRAYDESIGN_1.getLsidNamespace(), DUMMY_ARRAYDESIGN_1.getLsidObjectId());
            assertEquals(DUMMY_ARRAYDESIGN_1, retrievedArrayDesign);
            assertEquals(DUMMY_ARRAYDESIGN_1.getDesignFileSet().getFiles().size(), retrievedArrayDesign
                    .getDesignFileSet().getFiles().size());
            assertEquals(DUMMY_ARRAYDESIGN_1.getFirstDesignFile(), retrievedArrayDesign.getFirstDesignFile());
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_1.getLsidAuthority(),
                    DUMMY_ARRAYDESIGN_1.getLsidNamespace(), "incorrectObjectId");
            assertNull(retrievedArrayDesign);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    @Test
    public void testGetArrayData() {
        // normally arraydata would be associated with samples, but in this it is not
        // so disable the security filters
        this.hibernateHelper.setFiltersEnabled(false);
        Transaction tx = null;
        CaArrayFile file = new CaArrayFile();
        file.setFileStatus(FileStatus.UPLOADED);
        file.setDataHandle(DUMMY_HANDLE);
        final RawArrayData rawData = new RawArrayData();
        rawData.setDataFile(file);
        rawData.setName("test" + System.currentTimeMillis());

        tx = this.hibernateHelper.beginTransaction();
        this.daoObject.save(rawData);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        RawArrayData retrievedArrayData = (RawArrayData) this.daoObject.getArrayData(file.getId());
        assertEquals(rawData, retrievedArrayData);
        file = new CaArrayFile();
        file.setFileStatus(FileStatus.UPLOADED);
        file.setDataHandle(DUMMY_HANDLE);
        this.daoObject.save(file);
        tx.commit();
        tx = this.hibernateHelper.beginTransaction();
        retrievedArrayData = (RawArrayData) this.daoObject.getArrayData(file.getId());
        assertNull(retrievedArrayData);
        tx.commit();
    }

    /**
     * Tests retrieving array design providers and array designs by provider
     */
    @Test
    public void testArrayDesignsByProviderAndAssayType() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            final SortedSet<AssayType> assayTypes = new TreeSet<AssayType>();
            assayTypes.add(DUMMY_ASSAY_TYPE1);
            final List<ArrayDesign> org1Designs = this.daoObject.getArrayDesigns(DUMMY_ORGANIZATION, assayTypes, false);
            @SuppressWarnings("unused")
            final List<ArrayDesign> org2Designs = this.daoObject.getArrayDesigns(DUMMY_ORGANIZATION, assayTypes, true);
            final List<ArrayDesign> assayType1Designs = this.daoObject.getArrayDesigns(null, assayTypes, false);
            final List<ArrayDesign> org2assayType1Designs = this.daoObject.getArrayDesigns(DUMMY_ORGANIZATION2,
                    assayTypes, false);
            assayTypes.clear();
            assayTypes.add(DUMMY_ASSAY_TYPE2);
            final List<ArrayDesign> assayType2Designs = this.daoObject.getArrayDesigns(null, assayTypes, false);
            final List<ArrayDesign> org2assayType2Designs = this.daoObject.getArrayDesigns(DUMMY_ORGANIZATION2,
                    assayTypes, false);
            final List<ArrayDesign> org1assayType2Designs = this.daoObject.getArrayDesigns(DUMMY_ORGANIZATION,
                    assayTypes, false);
            assertNotNull(org1Designs);
            assertNotNull(assayType1Designs);
            assertEquals(3, assayType1Designs.size());
            assertNotNull(assayType2Designs);
            assertEquals(1, assayType2Designs.size());
            assertNotNull(org2assayType1Designs);
            assertEquals(2, org2assayType1Designs.size());
            assertNotNull(org2assayType2Designs);
            assertEquals(1, org2assayType2Designs.size());
            assertNotNull(org1assayType2Designs);
            assertEquals(0, org1assayType2Designs.size());
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    @Test
    public void testGetArrayDataType() {
        Transaction tx = null;
        ArrayDataTypeDescriptor testDescriptor = createTestArrayDataTypeDescriptor();
        final ArrayDataType arrayDataType = new ArrayDataType();
        arrayDataType.setName(testDescriptor.getName());
        arrayDataType.setVersion(testDescriptor.getVersion());
        try {
            tx = this.hibernateHelper.beginTransaction();
            final QuantitationType quantitationType1 = createTestQuantitationType(createTestQuantitationTypeDescriptor());
            final QuantitationType quantitationType2 = createTestQuantitationType(createTestQuantitationTypeDescriptor());
            this.daoObject.save(quantitationType1);
            this.daoObject.save(quantitationType2);
            assertNotSame(quantitationType1, quantitationType2);
            arrayDataType.getQuantitationTypes().add(quantitationType1);
            arrayDataType.getQuantitationTypes().add(quantitationType2);
            this.daoObject.save(arrayDataType);
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            this.hibernateHelper.getCurrentSession().clear();
            ArrayDataType retrievedArrayDataType = this.daoObject.getArrayDataType(testDescriptor);
            assertEquals(arrayDataType, retrievedArrayDataType);
            testDescriptor = new ArrayDataTypeDescriptor() {

                @Override
                public String getName() {
                    return "not in db";
                }

                @Override
                public List<QuantitationTypeDescriptor> getQuantitationTypes() {
                    return null;
                }

                @Override
                public String getVersion() {
                    return null;
                }

                @Override
                public boolean isEquivalent(ArrayDataType type) {
                    return false;
                }

            };
            retrievedArrayDataType = this.daoObject.getArrayDataType(testDescriptor);
            assertNull(retrievedArrayDataType);
            retrievedArrayDataType = this.daoObject.getArrayDataType(null);
            assertNull(retrievedArrayDataType);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    @Test
    public void testIsArrayDesignLocked() {
        Transaction tx = null;
        try {
            tx = this.hibernateHelper.beginTransaction();
            assertFalse(this.daoObject.isArrayDesignLocked(1L));
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during isArrayDesignLocked: " + e.getMessage());
        }
    }

    @Test
    public void testDeleteArrayDesignDetails() throws Exception {
        Transaction tx = null;
        this.hibernateHelper.setFiltersEnabled(false);
        try {
            tx = this.hibernateHelper.beginTransaction();
            ArrayDesign retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_2.getId());
            final CaArrayFile file2 = new CaArrayFile();
            file2.setName("newfile");
            file2.setFileStatus(FileStatus.IMPORTED);
            file2.setDataHandle(DUMMY_HANDLE);
            retrievedArrayDesign.getDesignFiles().clear();
            retrievedArrayDesign.addDesignFile(file2);
            final ArrayDesignDetails designDetails = new ArrayDesignDetails();
            retrievedArrayDesign.setDesignDetails(designDetails);
            this.daoObject.save(retrievedArrayDesign);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            retrievedArrayDesign = this.daoObject.getArrayDesign(DUMMY_ARRAYDESIGN_2.getId());
            assertEquals(1, retrievedArrayDesign.getDesignFiles().size());
            assertTrue(retrievedArrayDesign.getDesignFiles().iterator().next().getChildren().isEmpty());
            assertNotNull(retrievedArrayDesign.getDesignDetails());
            this.daoObject.deleteArrayDesignDetails(retrievedArrayDesign);
            tx.commit();
            assertNull(retrievedArrayDesign.getDesignDetails());
        } catch (final Exception e) {
            this.hibernateHelper.rollbackTransaction(tx);
            throw e;
        }
    }

    private ArrayDataTypeDescriptor createTestArrayDataTypeDescriptor() {
        return new ArrayDataTypeDescriptor() {
            private final String name = "name" + System.currentTimeMillis() + ":" + uniqueInt++;

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public String getVersion() {
                return "version";
            }

            @Override
            public List<QuantitationTypeDescriptor> getQuantitationTypes() {
                return null;
            }

            @Override
            public boolean isEquivalent(ArrayDataType arrayDataType) {
                return false;
            }
        };
    }

    @Test
    public void testGetQuantitationType() {
        Transaction tx = null;
        QuantitationTypeDescriptor testDescriptor = createTestQuantitationTypeDescriptor();
        final QuantitationType quantitationType = createTestQuantitationType(testDescriptor);
        try {
            tx = this.hibernateHelper.beginTransaction();
            this.daoObject.save(quantitationType);
            tx.commit();
            tx = this.hibernateHelper.beginTransaction();
            QuantitationType retrievedQuantitationType = this.daoObject.getQuantitationType(testDescriptor);
            assertEquals(quantitationType, retrievedQuantitationType);
            testDescriptor = new QuantitationTypeDescriptor() {

                @Override
                public DataType getDataType() {
                    return DataType.BOOLEAN;
                }

                @Override
                public String getName() {
                    return "not in db";
                }

            };
            retrievedQuantitationType = this.daoObject.getQuantitationType(testDescriptor);
            assertNull(retrievedQuantitationType);
            retrievedQuantitationType = this.daoObject.getQuantitationType(null);
            assertNull(retrievedQuantitationType);
            tx.commit();
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }

    private QuantitationTypeDescriptor createTestQuantitationTypeDescriptor() {
        return new QuantitationTypeDescriptor() {
            private final String name = "name" + System.currentTimeMillis() + ":" + uniqueInt++;

            @Override
            public String getName() {
                return this.name;
            }

            @Override
            public DataType getDataType() {
                return DataType.FLOAT;
            }
        };
    }

    private QuantitationType createTestQuantitationType(QuantitationTypeDescriptor descriptor) {
        final QuantitationType quantitationType = new QuantitationType();
        quantitationType.setName(descriptor.getName());
        quantitationType.setTypeClass(descriptor.getDataType().getTypeClass());
        return quantitationType;
    }

    @Test
    public void testGetArrayDesignsWithReImportable() {
        Transaction tx = null;

        try {
            tx = this.hibernateHelper.beginTransaction();
            final CaArrayFile f1 = new CaArrayFile();
            f1.setName("foo");
            f1.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
            f1.setDataHandle(DUMMY_HANDLE);
            f1.setFileType(AFFYMETRIX_CDF);
            DUMMY_ARRAYDESIGN_1.getDesignFiles().clear();
            DUMMY_ARRAYDESIGN_1.addDesignFile(f1);
            this.daoObject.save(f1);
            this.daoObject.save(DUMMY_ARRAYDESIGN_1);

            final CaArrayFile f2 = new CaArrayFile();
            f2.setName("bar");
            f2.setFileStatus(FileStatus.IMPORTED_NOT_PARSED);
            f2.setFileType(AFFYMETRIX_DAT);
            f2.setDataHandle(DUMMY_HANDLE);
            DUMMY_ARRAYDESIGN_2.getDesignFiles().clear();
            DUMMY_ARRAYDESIGN_2.addDesignFile(f2);
            this.daoObject.save(f2);
            this.daoObject.save(DUMMY_ARRAYDESIGN_2);
            tx.commit();

            tx = this.hibernateHelper.beginTransaction();
            assertTrue(DUMMY_ARRAYDESIGN_1.getDesignFiles().iterator().next().isUnparsedAndReimportable());
            assertTrue(DUMMY_ARRAYDESIGN_1.isUnparsedAndReimportable());
            assertFalse(DUMMY_ARRAYDESIGN_2.getDesignFiles().iterator().next().isUnparsedAndReimportable());
            assertFalse(DUMMY_ARRAYDESIGN_2.isUnparsedAndReimportable());

            final List<ArrayDesign> ads = this.daoObject.getArrayDesignsWithReImportable();
            assertEquals(1, ads.size());
            assertEquals(DUMMY_ARRAYDESIGN_1.getId(), ads.get(0).getId());
        } catch (final DAOException e) {
            this.hibernateHelper.rollbackTransaction(tx);
            fail("DAO exception during save and retrieve of arraydesign: " + e.getMessage());
        }
    }
}
