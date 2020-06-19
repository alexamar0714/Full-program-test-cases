package amberdb.enums;

import java.util.ArrayList;
import java.util.List;

public enum FileType {
    IMAGE_FILE("ImageFile", "image", "Image"),
    SOUND_FILE("SoundFile", "audio", "Sound"),
    MOVINGIMAGE_FILE("MovingImageFile", "video", "Moving Image");
    
    private String code;
    private String mimeTypePrefix;
    private String materialType;
    
    private FileType(String code, String format, String materialType) {
        this.code = code;
        this.mimeTypePrefix = format;
        this.materialType = materialType;
    }
    
    public String materialType() {
        return materialType;
    }
    
    public String format() {
        return mimeTypePrefix;
    }
    
    public String code() {
        return code;
    }
    
    public static FileType fromString(String code) {
        if (code != null) {
            for (FileType ft : FileType.values()) {
                if (code.equalsIgnoreCase(ft.code)) {
                    return ft;
                }
            }
        }
        return null;
    }

    public static List<String> list() {
        List<String> list = new ArrayList<String>();
        for (FileType lu : FileType.values()) {
            list.add(lu.code());
        }
        return list;
    }
}
