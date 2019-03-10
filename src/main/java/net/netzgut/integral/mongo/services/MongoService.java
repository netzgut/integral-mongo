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
package net.netzgut.integral.mongo.services;

import java.io.Serializable;

import org.bson.Document;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

public interface MongoService {

    /**
     * Returns the {@link com.mongodb.MongoClient} so you can use it directly.
     */
    MongoClient getMongoClient();

    /**
     * Returns the default database provided by the configuration.
     */
    MongoDatabase getDefaultDatabase();

    /**
     * Returns a specific database.
     */
    MongoDatabase getDatabase(String database);

    /**
     * Returns a Collection name by extracting the name from the entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     * @param ignoreNamingStrategy Ignores the {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * You should prefer using the a strategy, though.
     */
    String getCollectionName(Class<? extends Serializable> entityClass, boolean ignoreNamingStrategy);

    /**
     * Returns a Collection name by extracting the name from the entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     */
    default String getCollectionName(Class<? extends Serializable> entityClass) {
        return getCollectionName(entityClass, false);
    }

    /**
     * Returns a Collection name by name and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     */
    String getCollectionName(String collectionName);

    /**
     * Returns a Mongo Collection from the default database by extracting the name from the entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     * @param ignoreNamingStrategy Ignores the {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * You should prefer using the a strategy, though.
     * @return
     */
    default MongoCollection<Document> getCollection(Class<? extends Serializable> entityClass,
                                                    boolean ignoreNamingStrategy) {
        return getCollection(getDefaultDatabase(), entityClass, ignoreNamingStrategy);
    }

    /**
     * Returns a Collection from the default database by extracting the name from the entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     */
    default MongoCollection<Document> getCollection(Class<? extends Serializable> entityClass) {
        return getCollection(getDefaultDatabase(), entityClass, false);
    }

    /**
     * Returns a Collection from the default database by name and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param ignoreNamingStrategy Ignores the {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * You should prefer using the a strategy, though.
     */
    default MongoCollection<Document> getCollection(String collectionName, boolean ignoreNamingStrategy) {
        return getCollection(getDefaultDatabase(), collectionName, ignoreNamingStrategy);
    }

    /**
     * Returns a Collection from the default database by name and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     */
    default MongoCollection<Document> getCollection(String collectionName) {
        return getCollection(getDefaultDatabase(), collectionName, false);
    }

    /**
     * Returns a Collection from a specific Database by entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     * @param ignoreNamingStrategy Ignores the {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * You should prefer using the a strategy, though.
     */
    default MongoCollection<Document> getCollection(MongoDatabase db,
                                                    Class<? extends Serializable> entityClass,
                                                    boolean ignoreNamingStrategy) {
        return getCollection(db, getCollectionName(entityClass), ignoreNamingStrategy);
    }

    /**
     * Returns a Collection from a specific Database by entity class and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     */
    default MongoCollection<Document> getCollection(MongoDatabase db, Class<? extends Serializable> entityClass) {
        return getCollection(db, getCollectionName(entityClass));
    }

    /**
     * Returns a Collection from a specific Database by name and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     * @param ignoreNamingStrategy Ignores the {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * You should prefer using the a strategy, though.
     */
    MongoCollection<Document> getCollection(MongoDatabase db, String collectionName, boolean ignoreNamingStrategy);

    /**
     * Returns a Collection from a specific Database by name and the
     * {@link net.netzgut.integral.mongo.strategies.CollectionNamingStrategy}.
     * @param entityClass A class that might be annotated with {@link net.netzgut.integral.mongo.annotations.Collection}
     */
    default MongoCollection<Document> getCollection(MongoDatabase db, String collectionName) {
        return getCollection(db, collectionName, false);
    }

    /**
     * Setups a Collection in the default database based on it's annotations.
     */
    default void setupCollection(Class<? extends Serializable> entityClass) {
        setupCollection(getDefaultDatabase(), entityClass);
    }

    /**
     * Setups a Collection in the default database based on it's annotations and overrides the Collection name.
     */
    default void setupCollection(Class<? extends Serializable> entityClass, String collectionName) {
        setupCollection(getDefaultDatabase(), entityClass, collectionName);
    }

    /**
     * Caps a Collection in the default database to a specific size.
     */
    default void capCollection(Class<? extends Serializable> entityClass, long sizeInBytes) {
        capCollection(getDefaultDatabase(), entityClass, sizeInBytes);
    }

    /**
     * Caps a Collection in the default database to a specific size.
     */
    default void capCollection(String collectionName, long sizeInBytes) {
        capCollection(getDefaultDatabase(), collectionName, sizeInBytes);
    }

    /**
     * Caps a Collection in a specific database to a specific size.
     */
    default void capCollection(MongoDatabase db, Class<? extends Serializable> entityClass, long sizeInBytes) {
        capCollection(db, getCollectionName(entityClass), sizeInBytes);
    }

    /**
     * Caps a Collection in a specific database to a specific size.
     */
    void capCollection(MongoDatabase db, String collectionName, long sizeInBytes);

    /**
     * Setups a Collection in a specific Database based on it's annotations.
     */
    void setupCollection(MongoDatabase db, Class<? extends Serializable> entityClass);

    /**
     * Setups a Collection in a specific database based on it's annotations and overrides the Collection name.
     */
    void setupCollection(MongoDatabase db, Class<? extends Serializable> entityClass, String collectionName);

    /**
     * Setups all Collections in the default database based on classes found in the package restrictions.
     */
    default void autoSetup(String... packageRestrictions) {
        autoSetup(getDefaultDatabase(), packageRestrictions);
    }

    /**
     * Setups all Collections in a specific database based on classes found in the package restrictions.
     */
    void autoSetup(MongoDatabase db, String... packageRestrictions);

}
