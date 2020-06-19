package amberdb.model;

import java.util.Date;

import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

@TypeValue("ImageFile")
public interface ImageFile extends File {
    
    @Property("resolution")
    public String getResolution();

    @Property("resolution")
    public void setResolution(String resolution);
    
    // examples:
    //   inch
    //   cm
    @Property("resolutionUnit")
    public String getResolutionUnit();
    
    @Property("resolutionUnit")
    public void setResolutionUnit(String resolutionUnit);
    
    @Property("colourSpace")
    public String getColourSpace();
    
    @Property("colourSpace")
    public void setColourSpace(String colourSpace);
    
    @Property("orientation")
    public String getOrientation();
    
    @Property("orientation")
    public void setOrientation(String orientation);
    
    /**
     * In pixels
     */
    @Property("imageWidth")
    public Integer getImageWidth();
    
    /**
     * In pixels
     */
    @Property("imageWidth")
    public void setImageWidth(Integer imageWidth);
            
    /**
     * In pixels
     */
    @Property("imageLength")
    public Integer getImageLength();
    
    /**
     * In pixels
     */
    @Property("imageLength")
    public void setImageLength(Integer imageLength);    
    
    @Property("manufacturerMake")
    public String getManufacturerMake();
        
    @Property("manufacturerMake")
    public void setManufacturerMake(String manufacturerMake);
    
    @Property("manufacturerModelName")
    public String getManufacturerModelName();
        
    @Property("manufacturerModelName")
    public void setManufacturerModelName(String manufacturerModelName);
    
    @Property("manufacturerSerialNumber")
    public String getManufacturerSerialNumber();
        
    @Property("manufacturerSerialNumber")
    public void setManufacturerSerialNumber(String manufacturerSerialNumber);
    
    @Property("applicationDateCreated")
    public Date getApplicationDateCreated();
        
    @Property("applicationDateCreated")
    public void setApplicationDateCreated(Date applicationDateCreated);
    
    @Property("application")
    public String getApplication();
        
    @Property("application")
    public void setApplication(String application);
    
    @Property("dateDigitised")
    public Date getDateDigitised();
        
    @Property("dateDigitised")
    public void setDateDigitised(Date dateDigitised);
    
    @Property("samplesPerPixel")
    public String getSamplesPerPixel();
    
    @Property("samplesPerPixel")
    public void setSamplesPerPixel(String samplesPerPixel);
    
    @Property("bitDepth")
    public String getBitDepth();
        
    @Property("bitDepth")
    public void setBitDepth(String bitDepth);
    
    @Property("photometric")
    public String getPhotometric();
    
    @Property("photometric")
    public void setPhotometric(String photometric);
    
    @Property("location")
    public String getLocation();
    
    @Property("location")
    public void setLocation(String location);
    
    @Property("colourProfile")
    public String getColourProfile();    
    
    @Property("colourProfile")
    public void setColourProfile(String colourProfile);

    @Property("cpLocation")
    public String getCpLocation();    
    
    @Property("cpLocation")
    public void setCpLocation(String cpLocation);
    
    @Property("zoomLevel")
    public String getZoomLevel();
    
    @Property("zoomLevel")
    public void setZoomLevel(String zoomLevel);   
}
