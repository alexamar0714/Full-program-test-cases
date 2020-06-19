package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;
/**
 * Represents an organisation or a person for acknowledgement
 * @author bsingh
 *
 */
@TypeValue("Party")
public interface Party extends Node {

    @Property("name")
    public String getName();

    @Property("name")
    public void setName(String name);
    
    @Property("orgUrl")
    public String getOrgUrl();

    @Property("orgUrl")
    public void setOrgUrl(String orgUrl);
    
    @Property("logoUrl")
    public String getLogoUrl();

    @Property("logoUrl")
    public void setLogoUrl(String logoUrl);   
    
    @Property("suppressed")
    public Boolean isSuppressed();

    @Property("suppressed")
    public void setSuppressed(Boolean suppressed);
   
}
