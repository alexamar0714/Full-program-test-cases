package amberdb.util;

import amberdb.enums.CopyRole;
import amberdb.model.*;
import amberdb.model.File;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import java.io.*;
import java.util.List;
import java.util.zip.GZIPInputStream;

public class OcrCheck {
    Work work;
    Boolean hasImageCopy = null;
    ImageFile image = null;

    /**
     * OcrCheck constructor: construct an OcrCheck instance base on the input work.
     * @param work - a page containing image and ocr to be compared with or the parent work of the page to be checked.
     */
    public OcrCheck(Work work) {
        if (work == null) {
            throw new IllegalArgumentException("Input work is null.");
        }
        this.work = work;
    }

    /**
     * ocrOutOfBound: validate the input co-ords x, y, w, h are within the corresponding width and length of the image the ocr
     * @param x - the start value in x axis
     * @param y - the start value in y axis
     * @param w - the translation of x value by the width of the ocr bounding box
     * @param h - the translation of y value by the length of the ocr bounding box
     * @return indicator whether the generated ocr content is outside the image width and length.
     */
    public boolean ocrOutOfBound(int x, int y, int w, int h) {
        return ocrOutOfBound(getImage(), x, y, w, h);
    }

    /**
     * ocrOutOfBound: validate the generated ocr json in the jsonFile, and return true if the bounding box for the first paragraph
     *                is not within the corresponding width and length of the image the ocr is generated from. This method can be
     *                called before adding the jsonFile to the work's OCR_JSON_COPY.
     * @param jsonFile - the file containing generated ocr content in JSON format.
     * @return indicator whether the generated ocr content is outside the image width and length.
     * @throws UnsupportedEncodingException
     * @throws FileNotFoundException
     * @throws IOException
     */
    public boolean ocrOutOfBound(java.io.File jsonFile) throws UnsupportedEncodingException, FileNotFoundException, IOException {
        if (jsonFile == null) {
            throw new IllegalArgumentException("Input json file is null.");
        }
        return ocrOutOfBound(work, new FileInputStream(jsonFile));
    }

    /**
     * ocrOutOfBound: validate the generated ocr json in the work's ocr json copy, and return true if the bounding box for the first paragraph
     *                is not within the corresponding width and length of the image the ocr is generated from.  This method can be called to
     *                identify issues with existing generated ocr json.
     * @return indicator whether the generated ocr content is outside the image width and length.
     * @throws UnsupportedEncodingException
     * @throws IOException
     */
    public boolean ocrOutOfBound() throws UnsupportedEncodingException, IOException {
        return ocrOutOfBound(this.work);
    }

    protected ImageFile getImage() {
        if (hasImageCopy != null) {
            return image;
        }
        Copy pageImg = work.getCopy(CopyRole.CO_MASTER_COPY);
        if (pageImg == null) {
            pageImg = work.getCopy(CopyRole.MASTER_COPY);
        }
        if (pageImg != null) {
            hasImageCopy = true;
            image = pageImg.getImageFile();
        } else {
            hasImageCopy = false;
            image = null;
        }
        return image;
    }

    private boolean ocrOutOfBound(Work work) throws UnsupportedEncodingException, IOException {
        return ocrOutOfBound(work, null);
    }

    private boolean ocrOutOfBound(ImageFile image, int x, int y, int w, int h) {
        if (x < 0 || y < 0) {
            throw new RuntimeException("Invalid ocr bounding box x and y from (x: " + x + ", y: " + y + ")");
        }

        if (w <= 0 || h <= 0) {
            throw new RuntimeException("Invalid ocr bounding box width and length from (w: " + w + ", h: " + h + ")");
        }

        if (image == null) {
            throw new RuntimeException("No image is found for the work " + work.getObjId());
        }

        Integer imageWidth = image.getImageWidth();
        Integer imageLength = image.getImageLength();
        Integer resolutionX = null;
        String resolution = image.getResolution();
        try {
            if (resolution == null || resolution.isEmpty()) {
                throw new RuntimeException("Missing image resolution for page " + work.getObjId());
            }
            resolutionX = new Integer(resolution.split("x")[0].replaceAll("[^0-9]+",""));
            if (resolutionX <= 0) {
                throw new RuntimeException("Invalid image resolution " +  resolution + " for page " + work.getObjId());
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Invalid image resolution " +  resolution + " for page " + work.getObjId());
        }

        if (imageWidth == null || imageWidth <= 0) {
            throw new RuntimeException("Missing image width for page " + work.getObjId());
        }

        if (imageLength == null || imageLength <= 0) {
            throw new RuntimeException("Missing image length for page " + work.getObjId());
        }

        if (x > imageWidth || y > imageLength) {
            return true;
        }

        if (w > imageWidth || h > imageLength) {
            return true;
        }
        return false;
    }

    public boolean ocrOutOfBound(Work work, InputStream in) throws UnsupportedEncodingException, IOException {
        if (work instanceof Page) {
            ImageFile image = getImage();
            InputStream ocrStream = in;
            if (ocrStream == null) {
                Copy ocrJson = work.getCopy(CopyRole.OCR_JSON_COPY);
                if (ocrJson == null) {
                    return false;
                }

                File ocrJsonFile = ocrJson.getFile();
                if (ocrJsonFile == null) {
                    return false;
                }
                ocrStream = ocrJsonFile.openStream();
            }

            try (InputStreamReader isr = new InputStreamReader(new GZIPInputStream(ocrStream), "utf8")) {
                JsonNode ocr = new ObjectMapper().readTree(isr);
                JsonNode element = ocr.get("print").get("ps");
                if (element == null) {
                    element = ocr.get("print").get("zs");
                }
                ArrayNode ocrTextArry = (ArrayNode) element;
                if (ocrTextArry.size() > 0) {
                    String firstBoundingBox = ocrTextArry.get(0).get("b").asText();
                    String[] coOrds = firstBoundingBox.split(",");
                    if (coOrds == null || coOrds.length != 4) {
                        throw new RuntimeException("Invalid bounding box " + firstBoundingBox + " in ocr json for work " + work.getObjId());
                    }

                    try {
                        Integer startX = Integer.parseInt(coOrds[0]);
                        Integer startY = Integer.parseInt(coOrds[1]);
                        Integer widthBound = Integer.parseInt(coOrds[2]);
                        Integer lengthBound = Integer.parseInt(coOrds[3]);
                        return ocrOutOfBound(image, startX, startY, widthBound, lengthBound);
                    } catch (NumberFormatException e) {
                        throw new RuntimeException("Failed to parse ocr bounding box width and length from (w: " + coOrds[2] + ", h: " + coOrds[3] + ")");
                    }
                }
                return false;
            }
        } else {
            List<Work> pages = work.getPartsOf("Page");
            if (pages == null || pages.isEmpty()) {
                return false;
            }
            return ocrOutOfBound(pages.get(0), in);
        }
    }
}
