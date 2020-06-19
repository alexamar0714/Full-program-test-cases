package amberdb.util;


import static org.junit.Assert.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.joda.time.LocalDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import amberdb.enums.AccessCondition;
import amberdb.enums.CopyrightPolicy;
import amberdb.model.Work;


@RunWith(MockitoJUnitRunner.class)
public class WorkUtilsTest {
    
    @Mock
    private Work work;

    @Test
    public void testSetRestrictionsForFreelyAvailable() {
        when(work.getStartDate()).thenReturn(new Date());
        
        WorkUtils.setRestrictions(work, "immediately");
        
        verify(work).setCopyrightPolicy(CopyrightPolicy.INCOPYRIGHT.code());
        verify(work).setAccessConditions(AccessCondition.METADATA_ONLY.code());
        verify(work).setInternalAccessConditions(AccessCondition.OPEN.code());
        verify(work).setAllowHighResdownload(true);
        verify(work).setAllowOnsiteAccess(true);
        verify(work).setSensitiveMaterial("No");
    }

    @Test
    public void testSetRestrictionsForRestricted() {
        when(work.getStartDate()).thenReturn(new Date());
        
        WorkUtils.setRestrictions(work, "");
        
        verify(work).setCopyrightPolicy(CopyrightPolicy.INCOPYRIGHT.code());
        verify(work).setAccessConditions(AccessCondition.METADATA_ONLY.code());
        verify(work).setInternalAccessConditions(AccessCondition.RESTRICTED.code());
        verify(work).setAllowHighResdownload(false);
        verify(work).setAllowOnsiteAccess(true);
        verify(work).setSensitiveMaterial("No");
        verify(work).setExpiryDate(new LocalDate(9999, 1, 1).toDate());
    }
    
    @Test
    public void testSetRestrictionsForEmbargo() {
        LocalDate date = new LocalDate();
        when(work.getStartDate()).thenReturn(date.toDate());
        
        WorkUtils.setRestrictions(work, "12months");
        
        verify(work).setCopyrightPolicy(CopyrightPolicy.INCOPYRIGHT.code());
        verify(work).setAccessConditions(AccessCondition.METADATA_ONLY.code());
        verify(work).setInternalAccessConditions(AccessCondition.RESTRICTED.code());
        verify(work).setAllowHighResdownload(false);
        verify(work).setAllowOnsiteAccess(true);
        verify(work).setSensitiveMaterial("No");
        verify(work).setExpiryDate(date.plusMonths(12).toDate());
    }

}
