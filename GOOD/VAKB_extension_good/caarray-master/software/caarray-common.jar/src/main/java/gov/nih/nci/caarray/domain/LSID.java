//======================================================================================
// Copyright 5AM Solutions Inc, Yale University
//
// Distributed under the OSI-approved BSD 3-Clause License.
// See http://ncip.github.com/caarray/LICENSE.txt for details.
//======================================================================================
package gov.nih.nci.caarray.domain;

import java.io.Serializable;

/**
 * Life Science Identifier -- a unique identifier for entities. From Wikipedia:
 * 
 * <blockquote>Life Science Identifiers are a way to name and locate pieces of information on the web.
 * Essentially, an LSID is a unique identifier for some data, and the LSID protocol specifies a 
 * standard way to locate the data (as well as a standard way of describing that data).
 * 
 * <p>An LSID is represented as a Uniform Resource Name (URN) with the following format.
 * URN:LSID:&lt;Authority&gt;:&lt;Namespace&gt;:&lt;ObjectID&gt;[:&lt;Version&gt;] 
 * </blockquote>
 */
public final class LSID implements Serializable {

    private static final long serialVersionUID = 3343197687318404334L;

    private static final String PREFIX = "URN:LSID:";

    private final String authority;
    private final String namespace;
    private final String objectId;
    
    /**
     * Instantiates a complete, populated LSID.
     * 
     * @param authority the LSID authority
     * @param namespace the LSID namespace
     * @param objectId the LSID objectID
     */
    public LSID(final String authority, final String namespace, final String objectId) {
        this.authority = authority.intern();
        this.namespace = namespace.intern();
        this.objectId = objectId;
    }
    
    /**
     * Sets the LSID components for this entity.
     * If the authority and namespace are both absent, the default caArray authority
     * and namespace will be used. The LSID string is of the form authority:namespace:objectId
     * where authority can be absent, or authority and namespace can both be absent.
     *
     * @param lsidString the LSID string
     */
    public LSID(String lsidString) {
        String[] lsidPortions = lsidString.split(":");
        int lsidIndex = lsidPortions.length - 1;
        this.objectId = lsidPortions[lsidIndex];
        lsidIndex--;
        if (lsidIndex < 0) {
            // No explicit namespace provided. The name is assumed to be in the local (caArray) namespace.
            this.authority = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;
            this.namespace = AbstractCaArrayEntity.CAARRAY_LSID_NAMESPACE;
        } else {
            this.namespace = lsidPortions[lsidIndex];
            lsidIndex--;
            if (lsidIndex >= 0) {
                this.authority = lsidPortions[lsidIndex];
            } else {
                this.authority = AbstractCaArrayEntity.CAARRAY_LSID_AUTHORITY;                
            }
        }
    }


    /**
     * @return the authority
     */
    public String getAuthority() {
        return authority;
    }

    /**
     * @return the namespace
     */
    public String getNamespace() {
        return namespace;
    }

    /**
     * @return the objectId
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Returns the full formatted LSID string.
     * 
     * @return the formatted LSID.
     */
    @Override
    public String toString() {
        StringBuffer sb = 
            new StringBuffer(PREFIX.length() + authority.length() + namespace.length() + objectId.length() + 2);
        sb.append(PREFIX);
        sb.append(authority);
        sb.append(':');
        sb.append(namespace);
        sb.append(':');
        sb.append(objectId);
        return sb.toString();
    }

}
