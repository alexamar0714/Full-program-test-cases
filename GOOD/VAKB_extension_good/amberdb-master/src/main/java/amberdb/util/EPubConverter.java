package amberdb.util;


import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Convert an ebook into the EPub format using the Calibre ebook-convert command line tool.
 */
public class EPubConverter extends ExternalToolConverter {
    
    private static final Logger log = LoggerFactory.getLogger(EPubConverter.class);
    
    /** the path to the Calibre ebook-convert tool */
    private Path epubConverter;

    /**
     * Constructor.
     * @param epubConverter the path to the Calibre ebook-convert tool. must not be null.
     */
    public EPubConverter(Path epubConverter) {
        super();
        
        assert epubConverter != null;
        
        this.epubConverter = epubConverter;
    }
    
    /**
     * Convert the source file into an EPub formatted file using Calibre ebook-convert
     * @param srcFilePath   the source ebook file to convert
     * @param dstFilePath   the destination to write the converted ebook to
     * @throws ExternalToolException if the conversion failed
     */
    public void convertFile(Path srcFilePath, Path dstFilePath) throws ExternalToolException {
        log.debug("Preparing to convert {} to {}", srcFilePath, dstFilePath);
        
        if (srcFilePath == null || srcFilePath.getParent() == null || srcFilePath.getFileName() == null) {
            throw new RuntimeException("Invalid ebook file source path.");
        }
        if (dstFilePath == null || dstFilePath.getParent() == null || dstFilePath.getFileName() == null) {
            throw new RuntimeException("Invalid epub file destination path.");
        }
        String dstFilename = dstFilePath.getFileName().toString();
        if (!dstFilename.endsWith(".epub")) {
            throw new RuntimeException("Destination file (" + dstFilePath.toString() + ") must end with .epub");
        }
        
        executeCmd(epubConverter.toString(), srcFilePath.toString(), dstFilePath.toString());
    }

}
