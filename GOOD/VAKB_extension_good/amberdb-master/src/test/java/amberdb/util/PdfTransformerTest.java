package amberdb.util;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Paths;

import org.apache.tika.Tika;
import org.junit.Test;

public class PdfTransformerTest {
    @Test
    public void testGeneratePdf() throws IOException {
        Tika tika = new Tika();
        InputStream in = new FileInputStream(new File("src/test/resources/books.xml"));
        Reader stylesheets = new FileReader(Paths.get("src/test/resources/books.xsl").toFile());
        byte[] pdf = PdfTransformerFop.transform(in, stylesheets);
        assertTrue(tika.detect(pdf).equals("application/pdf"));
    }
}
