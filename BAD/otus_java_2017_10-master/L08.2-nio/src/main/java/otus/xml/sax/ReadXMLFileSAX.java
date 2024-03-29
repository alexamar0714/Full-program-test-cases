package otus.xml.sax;


import java.util.function.Supplier;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class ReadXMLFileSAX {
    public static Object readXML(String xmlFile, Supplier<BuilderHandler> handlerFactory) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();

            BuilderHandler handler = handlerFactory.get();
            saxParser.parse(xmlFile, handler);

            return handler.build();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
