//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.dao;

import static org.junit.Assert.assertEquals;
import gov.nih.nci.caarray.domain.contact.Organization;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.junit.Before;
import org.junit.Test;

/**
 * Test cases
 */
public class ContactDaoTest extends AbstractDaoTest {
    private ContactDao daoObject;

    @Before
    public void setup() {
        this.daoObject = new ContactDaoImpl(this.hibernateHelper);
    }

    @Test
    public void testGetAllProviders() {
        Transaction tx = this.hibernateHelper.beginTransaction();
        assertEquals(0, this.daoObject.getAllProviders().size());
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        Session s = this.hibernateHelper.getCurrentSession();
        final Organization o = new Organization();
        o.setName("Foo");
        o.setEmail("<a href="/cdn-cgi/l/email-protection" class="__cf_email__" data-cfemail="b4d2dbdbf4d6d5c69adbc6d3">[email protected]</a>");
        o.setProvider(true);
        s.save(o);
        tx.commit();

        tx = this.hibernateHelper.beginTransaction();
        s = this.hibernateHelper.getCurrentSession();
        assertEquals(1, this.daoObject.getAllProviders().size());
        assertEquals("Foo", this.daoObject.getAllProviders().get(0).getName());
        tx.commit();
    }
}
