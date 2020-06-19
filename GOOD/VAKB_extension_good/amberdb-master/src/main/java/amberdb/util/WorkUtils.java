package amberdb.util;

import java.io.IOException;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import org.joda.time.LocalDate;

import amberdb.enums.AccessCondition;
import amberdb.enums.CopyrightPolicy;
import amberdb.enums.DepositType;
import amberdb.model.Work;


public class WorkUtils {
    
    public static final String OPENACCESS = "openaccess";

    public static boolean checkCanReturnRepImage(Work work) {
        String accessCondition = work.getAccessConditions();
        if (!"unrestricted".equalsIgnoreCase(accessCondition)) {
            return false;
        }

        String internalAccesscondition = work.getInternalAccessConditions();
        if ("closed".equalsIgnoreCase(internalAccesscondition) || "restricted".equalsIgnoreCase(internalAccesscondition)) {
            return false;
        }

        String sensitiveMaterial = work.getSensitiveMaterial();
        return !"yes".equalsIgnoreCase(sensitiveMaterial);
    }
    
    /**
     * Set the access restrictions for the work. It assumes that all the relevant 
     * information for deciding the restrictions has been set in the work.
     * For the rules, see: DDG-83
     * @param work          the work to set the access restrictions on.
     * @param restriction   the access restriction to apply. must not be null.
     */
    public static void setRestrictions(Work work, String restriction) {
        boolean freelyAvailable = "immediately".equalsIgnoreCase(restriction); // immediately = freely available, Xmonths = freely available after X months, empty = restricted to copyright act
        boolean openAccess = OPENACCESS.equalsIgnoreCase(restriction);
        
        AccessCondition accessConditions;
        AccessCondition internalAccessConditions;
        Date expiryDate = null;
        boolean highResDownload;
        boolean allowOnsiteAccess = true;
        Set<String> constraint = new LinkedHashSet<String>();
        
        if (openAccess) {
            accessConditions = AccessCondition.UNRESTRICTED;
            internalAccessConditions = AccessCondition.OPEN;
            highResDownload = false;
        } else if (freelyAvailable) {
            accessConditions = AccessCondition.METADATA_ONLY;
            internalAccessConditions = AccessCondition.OPEN;
            expiryDate = new LocalDate(2016, 4, 17).toDate();
            constraint.add("edeposit online access coming soon");
            highResDownload = true;
        } else {
            accessConditions =  AccessCondition.METADATA_ONLY;
            internalAccessConditions = AccessCondition.RESTRICTED;
            highResDownload = false;
            
            if (restriction.matches("[0-9]+months")) {
                expiryDate = new LocalDate().plusMonths(Integer.parseInt(restriction.replace("months", ""))).toDate();
                constraint.add("edeposit online access after embargo");
            } else {
                if (DepositType.OnlineGovernment.toString().equalsIgnoreCase(work.getDepositType())) {
                    expiryDate = new LocalDate(work.getStartDate()).plusYears(50).toDate();
                } else {
                    expiryDate = new LocalDate(9999, 1, 1).toDate();
                }
                constraint.add("edeposit library access coming soon");
            }
        }
        
        work.setCopyrightPolicy(CopyrightPolicy.INCOPYRIGHT.code());
        work.setAccessConditions(accessConditions.code());
        work.setInternalAccessConditions(internalAccessConditions.code());
        work.setExpiryDate(expiryDate);
        try { work.setConstraint(constraint); } catch (IOException e) { throw new RuntimeException(e); /* should never occur */ }
        
        work.setAllowHighResdownload(highResDownload);
        work.setAllowOnsiteAccess(allowOnsiteAccess);
        work.setSensitiveMaterial("No");
    }
    
}
