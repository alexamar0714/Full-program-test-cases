package amberdb.model;

import java.util.Date;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("GeoCoding")
public interface GeoCoding extends Description {
    /*
     * gpsVersion: the version of the recorded GPS values, e.g. 2.000
     */
    @Property("gpsVersion")
    public String getGPSVersion();
    
    @Property("gpsVersion")
    public void setGPSVersion(String gpsVersion);
    
    @Property("latitude")
    public String getLatitude();
    
    @Property("latitude")
    public void setLatitude(String latitude);
    
    /*
     * latitudeRef: the orientation of the latitiude, e.g. N
     */
    @Property("latitudeRef")
    public String getLatitudeRef();
    
    @Property("latitudeRef")
    public void setLatitudeRef(String latitudeRef);
    
    @Property("longitude")
    public String getLongitude();
    
    @Property("longitude")
    public void setLongitude(String longitude);
    
    /*
     * longitudeRef: the orientation of the longitude, e.g. W
     */
    @Property("longitudeRef")
    public String getLongitudeRef();
    
    @Property("longitudeRef")
    public void setLongitudeRef(String longitudeRef);
    
    @Property("timestamp")
    public Date getTimeStamp();
    
    @Property("timestamp")
    public void setTimeStamp(Date timestamp);
    
    @Property("mapDatum")
    public String getMapDatum();
    
    @Property("mapDatum")
    public void setMapDatum(String mapDatum);
}
