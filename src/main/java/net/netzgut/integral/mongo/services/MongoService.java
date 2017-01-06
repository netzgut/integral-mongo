/**
 * Copyright 2017 Netzgut GmbH <info@netzgut.net>
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
package net.netzgut.integral.mongo.services;

import org.bson.Document;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public interface MongoService {

    MongoDatabase getDefaultDatabase();

    MongoDatabase getDatabase(String database);

    String getCollectionName(Class<?> entityClass);

    String getCollectionName(String collectionName);

    default MongoCollection<Document> getCollection(Class<?> entityClass) {
        return getCollection(getDefaultDatabase(), entityClass);
    }

    default MongoCollection<Document> getCollection(String collectionName) {
        return getCollection(getDefaultDatabase(), collectionName);
    }

    default MongoCollection<Document> getCollection(MongoDatabase db, Class<?> entityClass) {
        return getCollection(db, getCollectionName(entityClass));
    }

    MongoCollection<Document> getCollection(MongoDatabase db, String collectionName);

    default void setupCollection(Class<?> entityClass) {
        setupCollection(getDefaultDatabase(), entityClass);
    }

    default void setupCollection(Class<?> entityClass, String collectionName) {
        setupCollection(getDefaultDatabase(), entityClass, collectionName);
    }

    default void capCollection(Class<?> entityClass, long sizeInBytes) {
        capCollection(getDefaultDatabase(), entityClass, sizeInBytes);
    }

    default void capCollection(String collectionName, long sizeInBytes) {
        capCollection(getDefaultDatabase(), collectionName, sizeInBytes);
    }

    default void capCollection(MongoDatabase db, Class<?> entityClass, long sizeInBytes) {
        capCollection(db, getCollectionName(entityClass), sizeInBytes);
    }

    void capCollection(MongoDatabase db, String collectionName, long sizeInBytes);

    void setupCollection(MongoDatabase db, Class<?> entityClass);

    void setupCollection(MongoDatabase db, Class<?> entityClass, String collectionName);

    default void autoSetup(String... packageRestrictions) {
        autoSetup(getDefaultDatabase(), packageRestrictions);
    }

    void autoSetup(MongoDatabase db, String... packageRestrictions);

}
