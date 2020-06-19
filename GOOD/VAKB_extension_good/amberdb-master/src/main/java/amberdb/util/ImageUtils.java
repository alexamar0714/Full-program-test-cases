package amberdb.util;

import org.apache.commons.io.FilenameUtils;

public class ImageUtils {
    public static boolean isDngFile(String mimeType, String originalFilename) {
        return "image/x-adobe-dng".equals(mimeType)
                || "image/dng".equals(mimeType)
                || ("image/tiff".equals(mimeType) && FilenameUtils.getExtension(originalFilename).equalsIgnoreCase("dng"));
    }
}
