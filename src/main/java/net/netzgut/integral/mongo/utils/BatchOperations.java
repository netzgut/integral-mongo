package net.netzgut.integral.mongo.utils;

import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.InsertManyOptions;

public class BatchOperations {

    public static void replaceDocuments(MongoCollection<Document> collection, List<Document> documents) {

        // 1. Validation
        // BW decided emptying the collection via this method should always
        // be wrong, so ignore request if no new documents are coming.
        if (collection == null || documents == null || documents.isEmpty()) {
            return;
        }

        // 2. Remove old data
        collection.deleteMany(new Document());

        // 3. Insert new data
        collection.insertMany(documents, new InsertManyOptions().ordered(true));
    }
}
