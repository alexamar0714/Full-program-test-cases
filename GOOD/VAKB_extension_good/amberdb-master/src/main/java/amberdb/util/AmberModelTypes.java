package amberdb.util;


import java.util.List;

import com.google.common.collect.Lists;


public class AmberModelTypes {

    public static final List<String> WORK_TYPES = Lists.newArrayList("Work", "Page", "Section", "EADWork");  
    public static final List<String> COPY_TYPES = Lists.newArrayList("Copy");  
    public static final List<String> FILE_TYPES = Lists.newArrayList("File", "ImageFile", "SoundFile", "MovingImageFile");
    public static final List<String> DESC_TYPES = Lists.newArrayList("Description", "IPTC", "GeoCoding", "CameraData");  
    
    public static boolean isWork(String type) {
        if (WORK_TYPES.contains(type)) return true;
        return false;
    }
    
    public static boolean isCopy(String type) {
        if (COPY_TYPES.contains(type)) return true;
        return false;
    }
    
    public static boolean isFile(String type) {
        if (FILE_TYPES.contains(type)) return true;
        return false;
    }
    
    public static boolean isDescription(String type) {
        if (DESC_TYPES.contains(type)) return true;
        return false;
    }
}
