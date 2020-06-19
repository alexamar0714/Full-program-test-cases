package amberdb.util;


import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;

/*
 * This class is to convert an image (tiff or jpeg for now) to jpeg 2000 (.jp2),
 * and to make sure that the jp2 file can be delivered by the IIP image server.
 * For some reason, the IIP image server can't deliver, or deliver incorrectly a number of jp2 files
 * created by kakadu kdu_compress, and even imagemagick convert. They are:
 *  - Bitonal (black and white) images:
 *    + If the image has photometric = 0 (WhiteIsZero), IIP delivers an inverted image (black turns to white and white to black)
 *    + If the image has photometric = 1 (BlackIsZero), IIP delivers the white bits as grey.
 *  - Colour images with more than 3 channels (RGB). IIP mixes up the colours and delivers strange images.
 *  - Colour images with 16 or 24 bitdepth. IIP mixes up the colours and delivers strange images, or even can't deliver at all.
 *  - Colour images with a colour palette (photometric > 2). Kakadu can't create jp2 unless option -no_palette is used.
 *
 * The solution is to convert the original tiff to another tiff which has higher/lower bitdepth, reduced channels, etc.
 * and use this tiff to create jpeg2000 jp2.
 *  - For bitonal images: increase the bitdepth from 1 to 8, which will solve the inverted problem and make
 *    the image image look good in the delivery system.
 *  - For colour images with more than 3 channels (RGB): turn them in to 3 channels (RGB).
 *  - For colour images with 16 or 24 bitdepth: turn bitdepth into 8.
 *  - For colour images with a colour palette (photometric > 2): strip off the colour palette.
 *
 * It's also noticed that some colour images (tiff) can't be converted to the standard jp2 due to kakadu file format support.
 * Kakadu recommends converting them to .jpx instead:
 *     Error in Kakadu File Format Support:
 *         Attempting to write a colour description (colr) box which uses JPX extended
 *         features to the image header of a baseline JP2 file.  You might like to upgrade
 *         the application to write files using the `jpx_target' object, rather than
 *         `jp2_target'.
 * Currently amberdb and banjo don't support jpx. Adding support for jpx will take place at a later time.
 * For the mean time, we'll just use imagemagick convert to convert it to jp2.
 */
public class Jp2Converter extends ExternalToolConverter {
    private static final Logger log = LoggerFactory.getLogger(Jp2Converter.class);
    private static final String IMAGE_MAGICK_DNG_FORMAT = "DNG";
    private static final String MIME_TYPE_TIFF = "image/tiff";

    Path imgConverter;
    Path jp2Converter;

    public Jp2Converter(Path jp2Converter, Path imgConverter) {
        super();
        
        this.jp2Converter = jp2Converter;
        this.imgConverter = imgConverter;
    }
    
    /**
     * Method converts a tiff, dng or jpeg image to a jp2 Image. Firstly need
     * to convert a jpeg or dng image to a tiff. Then convert an tiff image to a
     * jp2 Image.
     * 
     * @param srcFilePath the Path of the image being converted to a jp2
     * @param dstFilePath the destination path of the jp2 image
     * @param imgInfoMap If the srcFilePath is already a tiff pass image information. Otherwise leave as null and ImageInfo will be calculated.
     * @param originalFilename The orginal filename from the source file.
     * @throws Exception Runtime exception if validation fails or fails to create the jp2 file from the command line or even IO exception. 
     */
    public Boolean convertFileToJp2Image(Path srcFilePath, Path dstFilePath, Map<String, String> imgInfoMap, String originalFilename) throws Exception {
        // Validate method params.
        if (dstFilePath == null || dstFilePath.getParent() == null || dstFilePath.getFileName() == null) {
            // Jpeg2000 file must end with .jp2 or .jpx
            throw new RuntimeException("Invalid Jpeg2000 file destination path.");
        }
        String dstFilename = dstFilePath.getFileName().toString();
        if (!dstFilename.endsWith(".jp2") && !dstFilename.endsWith(".jpx")) {
            throw new RuntimeException("Jpeg2000 file (" + dstFilePath.toString() + ") must end with .jp2 or .jpx");
        }
        
        Path tmpTiffFilePath = dstFilePath.getParent().resolve("tmp_" + dstFilePath.getFileName() + ".tif");
        
        try {
            ImageInfo imgInfo = new ImageInfo();
            srcFilePath = convertToTiffAndDetermineImageInfo(imgInfo, imgInfoMap, originalFilename, srcFilePath, tmpTiffFilePath);
            return convertTiffToJp2Image(srcFilePath, imgInfo, dstFilePath);
        } finally {
            // Delete tmp file if exists
            Files.deleteIfExists(tmpTiffFilePath);
        }
    }

    /*
     * Determine the ImageInfo of a tiff image. If the current image is not a
     * tiff image convert it to a tiff. Returns the path of the tiff image.
     */
    private Path convertToTiffAndDetermineImageInfo(ImageInfo imgInfo, Map<String, String> imgInfoMap, String originalFilename,
            Path srcFilePath, Path tmpFilePath) throws Exception {

        if (imgInfoMap != null && imgInfoMap.size() > 0) {
            // if imgInfoMap has data then the image is already a tiff, therefore update the object.
            imgInfo.updateImageInfo(imgInfoMap);
            return srcFilePath;
        }

        // Determine the image type
        String mimeType = tika.detect(srcFilePath.toFile());

        if (!MIME_TYPE_TIFF.equalsIgnoreCase(mimeType)) {
            // If image is not a tiff then convert/uncompress to a tiff.
            convertToTiff(mimeType, originalFilename, srcFilePath, tmpFilePath);
            
            // Point the src file to the tiff file
            srcFilePath = tmpFilePath;
        }

        // Now update the image info object
        imgInfo.updateImageInfo(srcFilePath);
        
        return srcFilePath;
    }
    
    /*
     * Converts either a jpg or DNG file to a tiff image.
     */
    private void convertToTiff(String mimeType, String originalFilename, Path srcFilePath, Path tmpFilePath) throws Exception {
        
        if ("image/jpeg".equals(mimeType)) {
            // Jpeg  - Convert to uncompressed tiff so kakadu can convert it to jp2000
            convertUncompress(srcFilePath, null, tmpFilePath);
            log.info("Converted jpeg {} to Tiff {}", srcFilePath, tmpFilePath);
        } else if (ImageUtils.isDngFile(mimeType, originalFilename)) {
            // DNG - Convert to uncompressed tiff so kakadu can convert it to jp2000
            // We need to explicitly tell ImageMagick that the input file is a DNG, because DNGs often look like
            // TIFFs (use the same mime type and magic number), so IM will use its TIFF converter which, for some
            // reason, loses dimension information during conversion, resulting in a very small image. Using the DNG
            // format hint makes IM use its DNG converter (ufraw).
            //
            // We also try to convert the image to the AdobeRGB colour profile. This requires the ICC file to be on
            // the classpath.
            String profileFile = "imageConversion/AdobeRGB1998.icc";
            URL colourProfile = getClass().getClassLoader().getResource(profileFile);
            if (colourProfile == null) {
                log.warn("Failed to load colour profile .icc file {}. Using ImageMagick defaults", profileFile);
                convertUncompress(srcFilePath, IMAGE_MAGICK_DNG_FORMAT, tmpFilePath);
            } else {
                // note that -profile MUST be provided twice (once for input, which is ignored but has to be there, and once for the output format)
                convertUncompress(srcFilePath, IMAGE_MAGICK_DNG_FORMAT, tmpFilePath, "-profile", colourProfile.getFile(), "-profile", colourProfile.getFile());
            }
            log.info("Converted DNG {} to Tiff {}", srcFilePath, tmpFilePath);
        } else {
            log.error("Can not convert the mime type {} to a Tiff", mimeType);
            throw new RuntimeException("Unsupported Mime Type");
        }
        
    }

    private Boolean convertTiffToJp2Image(Path srcFilePath, ImageInfo imgInfo, Path dstFilePath) throws Exception {
        try {
            return performConvertFile(srcFilePath, imgInfo, dstFilePath);
        } catch (Exception e) {
            log.warn("Retrying jp2 creation from source {} as the following exception has occurred: {}", srcFilePath.getFileName(), e.getMessage());
            Files.deleteIfExists(dstFilePath);
            Path tmpFilePath = dstFilePath.getParent().resolve("tmp_" + dstFilePath.getFileName() + "_retry.tif");
            
            try {
                convertStripProfile(srcFilePath, tmpFilePath);
                return performConvertFile(tmpFilePath, imgInfo, dstFilePath);
            } finally {
                Files.deleteIfExists(tmpFilePath);
            }
        }
    }
    
    /*
     * Convert a tiff image to a jp2 Image. Some modifications my need to occur for the kakadu image converter
     * to work correctly.
     */
    private Boolean performConvertFile(Path srcFilePath, ImageInfo imgInfo, Path dstFilePath) throws Exception {

        // Only convert tiff to jp2
        if (!"image/tiff".equalsIgnoreCase(imgInfo.mimeType)) {
            throw new RuntimeException("Not a tiff file");
        }
        
        long startTime = System.currentTimeMillis();
        
        Path modTiffFilePath = dstFilePath.getParent().resolve("mod_" + dstFilePath.getFileName() + ".tif");

        try {
            
            try {
                if (imgInfo.photometric == 5) {
                    // Don't convert file if the colour profile is CMYK ( photometric == 5)
                    log.info("JP2 convert of {} stopped due to CMYK colour profile", srcFilePath.toString());
                    return false;
                } else if (imgInfo.samplesPerPixel == 1 && imgInfo.bitsPerSample == 1) {
                    // Bitonal image - Convert to greyscale (8 bit depth)
                    convertBitdepth(srcFilePath, modTiffFilePath, 8);
                } else if (imgInfo.samplesPerPixel > 3) {
                    // Image has more than 3 channels (RGB) - Convert to 3 channels (TrueColor)
                    convertTrueColour(srcFilePath, modTiffFilePath);
                } else if (imgInfo.samplesPerPixel > 1 && imgInfo.bitsPerSample > 8) {
                    // Colour image with 16 or 24 bit depth - convert to 8 bit depth
                    convertBitdepth(srcFilePath, modTiffFilePath, 8);
                } else if (imgInfo.photometric > 2) {
                    if (imgInfo.photometric == 6) {
                        // YCbCr image - simply uncompress it and by doing so, make it RGB!
                        convertUncompress(srcFilePath, null, modTiffFilePath);
                    } else {
                        // Image has colour palette - Convert to 3 TrueColor
                        convertTrueColour(srcFilePath, modTiffFilePath);
                    }
                } else if (imgInfo.compression > 1) {
                    // Uncompress image as the demo app kdu_compress can't process compressed tiff
                    convertUncompress(srcFilePath, null, modTiffFilePath);
                }
            } catch (Exception e) {
                log.warn("Exception occurred when attempting to manipulate image into a JP2'able state.");
            }

            Path fileToCreateJp2 = Files.exists(modTiffFilePath) ? modTiffFilePath : srcFilePath;

            // Create jp2 using kakadu kdu_compress
            createJp2(fileToCreateJp2, dstFilePath);
        } catch (Exception e) {
            log.error("Error creating jp2 image {}", dstFilePath, e);
            throw e;
        } finally {
            // Delete tmp file if exists
            Files.deleteIfExists(modTiffFilePath);
        }

        long endTime = System.currentTimeMillis();
        log.debug("***Convert {} to {} took {} milliseconds", srcFilePath.toString(), dstFilePath.toString(), (endTime - startTime));
        return true;
    }

    // Convert the bit depth of an image
    private void convertBitdepth(Path srcFilePath, Path dstFilePath, int bitDepth) throws Exception {
        convertImage(srcFilePath, null, dstFilePath, "-compress", "None", "-depth", "" + bitDepth);
    }

    // Convert an image to true colour
    private void convertTrueColour(Path srcFilePath, Path dstFilePath) throws Exception {
        convertImage(srcFilePath, null, dstFilePath, "-compress", "None", "-type", "TrueColor");
    }

    // Uncompress an image
    private void convertUncompress(Path srcFilePath, String sourceFormatIndicator, Path dstFilePath, String... otherArgs) throws Exception {
        String[] args = new String[] {"-compress", "None"}; // kakadu cannot convert compressed tiffs, and ImageMagick compresses by default.
        if (otherArgs != null) {
            args = ArrayUtils.addAll(args, otherArgs);
        }
        convertImage(srcFilePath, sourceFormatIndicator, dstFilePath, args);
    }
    
    // Remove extra Tiff header fields
    private void convertStripProfile(Path srcFilePath, Path dstFilePath) throws Exception {
        convertImage(srcFilePath, null, dstFilePath, "-strip");
    }

    // Convert an image with imagemagick
    private void convertImage(Path srcFilePath, String sourceFormatIndicator, Path dstFilePath, String... params) throws Exception {
        // Setup command

        String sourceFile;
        if (isNotBlank(sourceFormatIndicator)) {
            sourceFile = sourceFormatIndicator.toLowerCase() + ":" + srcFilePath.toString();
        } else {
            sourceFile = srcFilePath.toString();
        }

        String[] cmd = new String[params.length + 3];
        cmd[0] = imgConverter.toString();
        System.arraycopy(params, 0, cmd, 1, params.length);
        cmd[cmd.length - 2] = sourceFile;
        cmd[cmd.length - 1] = dstFilePath.toString();

        // And execute it
        executeCmd(cmd);
    }

    // Create jp2 with kakadu kdu_compress
    private void createJp2(Path srcFilePath, Path dstFilePath) throws Exception {
        // Use kakadu to create jp2
        executeCmd(jp2Converter.toString(),
                "-i",
                srcFilePath.toString(),
                "-o",
                dstFilePath.toString(),
                "-rate",
                "0.5",
                "Clayers=1",
                "Clevels=7",
                "Cprecincts={256,256},{256,256},{256,256},{128,128},{128,128},{64,64},{64,64},{32,32},{16,16}",
                "Corder=RPCL",
                "ORGgen_plt=yes",
                "Cblk={32,32}",
                "-num_threads",
                "1",
                "Cuse_sop=yes");
    }

    class ImageInfo {
        String mimeType;
        int compression, samplesPerPixel, bitsPerSample, photometric;
                
        public void updateImageInfo(Map<String, String> imgInfoMap) {
            this.mimeType  = imgInfoMap.get("mimeType");
            this.compression = Integer.parseInt(imgInfoMap.get("compression"), 10);
            this.samplesPerPixel = Integer.parseInt(imgInfoMap.get("samplesPerPixel"), 10);
            this.bitsPerSample = Integer.parseInt(imgInfoMap.get("bitsPerSample"), 10);
            this.photometric = Integer.parseInt(imgInfoMap.get("photometric"), 10);
        }

        public void updateImageInfo(Path filePath) throws Exception {
            this.mimeType = tika.detect(filePath.toFile());;
            
            // Read image metadata using metadata-extractor - only for tiff
            Metadata metadata = ImageMetadataReader.readMetadata(filePath.toFile());
            ExifIFD0Directory directory = metadata.getDirectory(ExifIFD0Directory.class);
            if (directory == null) {
                throw new Exception("Missing ExifIFD0Directory: " + filePath.toString());
            }

            // Compression (259)
            this.compression = getTagValue(directory, 259);

            // Samples per pixel (277)
            this.samplesPerPixel = getTagValue(directory, 277);

            // Bits per sample (258)
            this.bitsPerSample = getTagValue(directory, 258);

            // Photometric (262)
            this.photometric = getTagValue(directory, 262);
        }

        private int getTagValue(ExifIFD0Directory directory, int tagNo) {
            return directory.getIntArray(tagNo)[0];
        }
    }
}
