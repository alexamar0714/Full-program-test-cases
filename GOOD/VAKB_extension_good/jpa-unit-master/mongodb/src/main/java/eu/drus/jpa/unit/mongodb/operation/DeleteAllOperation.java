package eu.drus.jpa.unit.mongodb.operation;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public class DeleteAllOperation implements MongoDbOperation {

    @Override
    public void execute(final MongoDatabase connection, final Document data) {
        for (final String collectionName : data.keySet()) {
            final MongoCollection<Document> collection = connection.getCollection(collectionName);
            collection.deleteMany(new Document());
        }
    }
}
