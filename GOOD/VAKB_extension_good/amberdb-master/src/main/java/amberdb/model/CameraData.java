package amberdb.model;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("CameraData")
public interface CameraData extends Description {
    @Property("exposureTime")
    public String getExposureTime();
    
    @Property("exposureTime")
    public void setExposureTime(String exposureTime);
    
    @Property("exposureFNumber")
    public String getExposureFNumber();
    
    @Property("exposureFNumber")
    public void setExposureFNumber(String exposureFNumber);
    
    @Property("exposureMode")
    public String getExposureMode();
    
    @Property("exposureMode")
    public void setExposureMode(String exposureMode);
    
    @Property("exposureProgram")
    public String getExposureProgram();
    
    @Property("exposureProgram")
    public void setExposureProgram(String exposureProgram);
    
    @Property("isoSpeedRating")
    public String getIsoSpeedRating();
    
    @Property("isoSpeedRating")
    public void setIsoSpeedRating(String isoSpeedRating);
    
    @Property("focalLength")
    public String getFocalLength();
    
    @Property("focalLength")
    public void setFocalLength(String focalLength);
    
    @Property("lens")
    public String getLens();
    
    @Property("lens")
    public void setLens(String lens);
    
    @Property("meteringMode")
    public String getMeteringMode();
    
    @Property("meteringMode")
    public void setMeteringMode(String lens);
    
    @Property("whiteBalance")
    public String getWhiteBalance();
    
    @Property("whiteBalance")
    public void setWhiteBalance(String whiteBalance);
    
    @Property("fileSource")
    public String getFileSource();
    
    @Property("fileSource")
    public void setFileSource(String fileSource);
    
}
