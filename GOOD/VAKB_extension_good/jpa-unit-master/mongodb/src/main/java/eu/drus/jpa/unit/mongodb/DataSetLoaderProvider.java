package eu.drus.jpa.unit.mongodb;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import org.bson.Document;

import eu.drus.jpa.unit.spi.DataSetFormat.LoaderProvider;
import eu.drus.jpa.unit.spi.DataSetLoader;
import eu.drus.jpa.unit.spi.UnsupportedDataSetFormatException;

public class DataSetLoaderProvider implements LoaderProvider<Document> {

    @Override
    public DataSetLoader<Document> xmlLoader() {
        throw new UnsupportedDataSetFormatException("XML data sets are not supportred for MongoDB");
    }

    @Override
    public DataSetLoader<Document> yamlLoader() {
        throw new UnsupportedDataSetFormatException("YAML data sets are not supportred for MongoDB");
    }

    @Override
    public DataSetLoader<Document> jsonLoader() {
        return (final File path) -> Document.parse(new String(Files.readAllBytes(path.toPath()), StandardCharsets.UTF_8));
    }

    @Override
    public DataSetLoader<Document> csvLoader() {
        throw new UnsupportedDataSetFormatException("CSV data sets are not supportred for MongoDB");
    }

    @Override
    public DataSetLoader<Document> xlsLoader() {
        throw new UnsupportedDataSetFormatException("XSL data sets are not supportred for MongoDB");
    }
}
