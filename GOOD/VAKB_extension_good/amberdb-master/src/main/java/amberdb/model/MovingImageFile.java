package amberdb.model;

import com.tinkerpop.blueprints.Vertex;
import com.tinkerpop.frames.Property;
import com.tinkerpop.frames.modules.javahandler.JavaHandler;
import com.tinkerpop.frames.modules.javahandler.JavaHandlerContext;
import com.tinkerpop.frames.modules.typedgraph.TypeValue;

import amberdb.util.DurationUtils;

@TypeValue("MovingImageFile")
public interface MovingImageFile extends File {

    @Property("surface")
    public String getSurface();    
    @Property("surface")
    public void setSurface(String surface);

    @Property("carrierCapacity")
    public String getCarrierCapacity();
    @Property("carrierCapacity")
    public void setCarrierCapacity(String carrierCapacity);    

    @Property("reelSize")
    public String getReelSize();    
    @Property("reelSize")
    public void setReelSize(String reelSize);

    @Property("channel")
    public String getChannel();    
    @Property("channel")
    public void setChannel(String channel);

    @Property("soundField")
    public String getSoundField();    
    @Property("soundField")
    public void setSoundField(String soundField);

    @Property("speed")
    public String getSpeed();    
    @Property("speed")
    public void setSpeed(String speed);

    @Property("thickness")
    public String getThickness();    
    @Property("thickness")
    public void setThickness(String thickness);

    @Property("brand")
    public String getBrand();    
    @Property("brand")
    public void setBrand(String brand);

    @Property("durationType")
    public String getDurationType();    
    @Property("durationType")
    public void setDurationType(String durationType);

    @Property("duration")
    public String getDuration();    
    @Property("duration")
    public void setDuration(String duration);

    @Property("equalisation")
    public String getEqualisation();    
    @Property("equalisation")
    public void setEqualisation(String equalisation);

    @Property("blockAlign")
    public Integer getBlockAlign();    
    @Property("blockAlign")
    public void setBlockAlign(Integer blockAlign);

    @Property("framerate")
    public Integer getFramerate();    
    @Property("framerate")
    public void setFramerate(Integer framerate);

    @Property("fileContainer")
    public String getFileContainer();    
    @Property("fileContainer")
    public void setFileContainer(String fileContainer);

    @Property("bitDepth")
    public String getBitDepth();
    @Property("bitDepth")
    public void setBitDepth(String bitDepth);

    @Property("bitrate")
    public String getBitrate();    
    @Property("bitrate")
    public void setBitrate(String bitrate);

    @Property("codec")
    public String getCodec();    
    @Property("codec")
    public void setCodec(String codec);

    @Property("samplingRate")
    public String getSamplingRate();    
    @Property("samplingRate")
    public void setSamplingRate(String samplingRate);

    @JavaHandler
    public Float getDurationAsSeconds();

    @JavaHandler
    public void setDurationAsSeconds(Float durationAsSeconds);
    
    @JavaHandler
    public String getDurationAsHHMMSS();

    abstract class Impl extends Node.Impl implements JavaHandlerContext<Vertex>, MovingImageFile {

        @Override
        public String getDurationAsHHMMSS(){
            return DurationUtils.convertDuration(getDuration());
        }
        
        @Override
        public Float getDurationAsSeconds() {
            return DurationUtils.convertDurationToSeconds(getDuration());
        }

        @Override
        public void setDurationAsSeconds(Float durationAsSeconds) {
            setDuration(DurationUtils.convertDurationFromSeconds(durationAsSeconds));
        }
    }
}
