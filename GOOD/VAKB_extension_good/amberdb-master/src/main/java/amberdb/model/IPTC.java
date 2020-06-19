package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("IPTC")
public interface IPTC extends Description {
    @Property("alternativeTitle")
    public String getAlternativeTitle();
    
    @Property("alternativeTitle")
    public void setAlternativeTitle(String alternativeTitle);
    
    @Property("subLocation")
    public String getSubLocation();
    
    @Property("subLocation")
    public void setSubLocation(String subLocation);
    
    @Property("city")
    public String getCity();
    
    @Property("city")
    public void setCity(String city);
    
    @Property("province")
    public String getProvince();
    
    @Property("province")
    public void setProvince(String province);
    
    @Property("country")
    public String getCountry();
    
    @Property("country")
    public void setCountry(String country);
    
    @Property("isoCountryCode")
    public String getISOCountryCode();
    
    @Property("isoCountryCode")
    public void setISOCountryCode(String isoCountryCode);
    
    @Property("worldRegion")
    public String getWorldRegion();
    
    @Property("worldRegion")
    public void setWorldRegion(String worldRegion);
    
    @Property("digitalSourceType")
    public String getDigitalSourceType();
    
    @Property("digitalSourceType")
    public void setDigitalSourceType(String digitalSourceType);
    
    @Property("event")
    public String getEvent();
    
    @Property("event")
    public void setEvent(String event);
    
    @Property("fileFormat")
    public String getFileFormat();
    
    @Property("fileFormat")
    public void setFileFormat(String fileFormat);
}
