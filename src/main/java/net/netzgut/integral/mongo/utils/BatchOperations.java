/**
 * Copyright 2018 Netzgut GmbH <info@netzgut.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
