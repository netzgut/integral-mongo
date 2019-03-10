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
package net.netzgut.integral.mongo.internal.services;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import net.netzgut.integral.mongo.annotations.Collection;
import net.netzgut.integral.mongo.annotations.Index;
import net.netzgut.integral.mongo.configuration.MongoConfiguration;
import net.netzgut.integral.mongo.services.MongoService;
import net.netzgut.integral.mongo.strategies.CollectionNamingStrategy;

public class MongoServiceImplementation implements MongoService, Closeable {

    private static final Logger                              log                         =
        LoggerFactory.getLogger(MongoServiceImplementation.class);

    private final MongoClient                                mongoClient;
    private final MongoDatabase                              defaultDatabase;
    private final CollectionNamingStrategy                   collectionNamingStrategy;

    private final Map<Class<? extends Serializable>, String> collectionNameCacheByClass  = new HashMap<>();
    private final Map<String, String>                        collectionNameCacheByString = new HashMap<>();

    public MongoServiceImplementation(MongoConfiguration configuration,
                                      CollectionNamingStrategy collectionNamingStrategy,
                                      RegistryShutdownHub shutdownHub) {
        this.collectionNamingStrategy = collectionNamingStrategy;
        if (configuration.getCredentials().isEmpty()) {
            this.mongoClient = new MongoClient(configuration.getServerAddress(), configuration.getClientOptions());
        }
        else {
            this.mongoClient = new MongoClient(configuration.getServerAddress(),
                                               configuration.getCredentials(),
                                               configuration.getClientOptions());
        }
        this.defaultDatabase = this.mongoClient.getDatabase(configuration.getDatabaseName());

        // call Closeable.close() on registry shutdown
        shutdownHub.addRegistryWillShutdownListener(() -> {
            try {
                close();
            }
            catch (IOException e) {
                log.warn("could not close MongoClient", e);
            }
        });
    }

    @Override
    public MongoClient getMongoClient() {
        return this.mongoClient;
    }

    @Override
    public MongoDatabase getDefaultDatabase() {
        if (this.defaultDatabase == null) {
            log.error("No default database set");
            throw new UnsupportedOperationException("No default database is set!");
        }
        return this.defaultDatabase;
    }

    @Override
    public MongoDatabase getDatabase(String database) {
        if (database == null || database.length() == 0) {
            log.error("Database name can't be blank");
            throw new IllegalArgumentException("Database name can't be blank");
        }

        return this.mongoClient.getDatabase(database);
    }

    @Override
    public String getCollectionName(Class<? extends Serializable> entityClass, boolean ignoreNamingStrategy) {
        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }

        if (ignoreNamingStrategy == false
            && this.collectionNamingStrategy.isCacheable()
            && this.collectionNameCacheByClass.containsKey(entityClass)) {
            return this.collectionNameCacheByClass.get(entityClass);
        }

        Collection annotation = findSingleAnnotation(Collection.class, entityClass);

        if (annotation == null) {
            String message = String.format("Annotation '@Collection' not present on class '%s'", entityClass.getName());
            log.error(message);
            throw new UnsupportedOperationException(message);
        }

        String collectionName = annotation.value();
        if (collectionName == null || collectionName.length() == 0) {
            collectionName = entityClass.getName();
        }

        if (ignoreNamingStrategy) {
            return collectionName;
        }

        String realCollectionName = getCollectionName(collectionName);
        if (this.collectionNamingStrategy.isCacheable()) {
            this.collectionNameCacheByClass.put(entityClass, collectionName);
        }

        return realCollectionName;
    }

    @Override
    public String getCollectionName(String collectionName) {
        if (this.collectionNamingStrategy.isCacheable()
            && this.collectionNameCacheByString.containsKey(collectionName)) {
            return this.collectionNameCacheByString.get(collectionName);
        }

        String realCollectionName = this.collectionNamingStrategy.name(collectionName);

        if (this.collectionNamingStrategy.isCacheable()) {
            this.collectionNameCacheByString.put(collectionName, realCollectionName);
        }
        return realCollectionName;
    }

    @Override
    public MongoCollection<Document> getCollection(MongoDatabase db,
                                                   String collectionName,
                                                   boolean ignoreNamingStrategy) {
        if (db == null) {
            log.error("Database can't be null");
            throw new IllegalArgumentException("Database can't be null");
        }

        if (collectionName == null || collectionName.length() == 0) {
            log.error("Collection name can't be blank");
            throw new IllegalArgumentException("Collection name can't be blank");
        }

        String finalCollectionName = ignoreNamingStrategy ? collectionName : getCollectionName(collectionName);

        return db.getCollection(finalCollectionName);
    }

    @Override
    public void capCollection(MongoDatabase db, String collectionName, long sizeInBytes) {
        final MongoIterable<String> result = db.listCollectionNames();
        final List<String> names = result.into(new ArrayList<>());

        if (names.contains(collectionName)) {
            final Document getStats = new Document("collStats", collectionName);
            final Document stats = db.runCommand(getStats);
            Object capped = stats.get("capped");
            final boolean isCapped = capped != null && capped.equals(1);
            if (isCapped == false) {
                final Document convertToCapped = new Document();
                convertToCapped.append("convertToCapped", collectionName);
                convertToCapped.append("size", sizeInBytes);
                db.runCommand(convertToCapped);

                // We need to create the index manually after conversion.
                // See red warning box: http://docs.mongodb.org/v2.2/reference/command/convertToCapped/#dbcmd.convertToCapped
                db.getCollection(collectionName).createIndex(new Document("_id", 1));
            }
        }
        else {
            db.createCollection(collectionName, new CreateCollectionOptions().capped(true).sizeInBytes(sizeInBytes));
        }

    }

    @Override
    public void setupCollection(MongoDatabase db, Class<? extends Serializable> entityClass) {
        if (db == null) {
            log.error("Database can't be null");
            throw new IllegalArgumentException("Database can't be null");
        }

        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }
        String collectionName = getCollectionName(entityClass);
        setupCollection(db, entityClass, collectionName);

    }

    @Override
    public void setupCollection(MongoDatabase db, Class<? extends Serializable> entityClass, String collectionName) {
        if (db == null) {
            log.error("Database can't be null");
            throw new IllegalArgumentException("Database can't be null");
        }

        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }

        if (collectionName == null || collectionName.length() == 0) {
            log.error("Collection name can't be blank");
            throw new IllegalArgumentException("Collection name can't be blank");
        }

        log.debug("setupCollection(DB: {}, Entity: {}, Collection: {}",
                  db.getName(),
                  entityClass.getName(),
                  collectionName);

        setupIndexes(entityClass, getCollection(db, entityClass));

    }

    @SuppressWarnings("unchecked")
    @Override
    public void autoSetup(MongoDatabase db, String... packageRestrictions) {
        log.debug("autoSetup(DB: {}, Package restrictions: {}", db.getName(), packageRestrictions);

        new FastClasspathScanner(packageRestrictions).matchClassesWithAnnotation(Collection.class, matchingClass -> {
            Collection annotation = matchingClass.getAnnotation(Collection.class);
            if (annotation.autoSetup()) {
                setupCollection(db, (Class<? extends Serializable>) matchingClass, annotation.value());
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.mongoClient.close();
    }

    private void setupIndexes(Class<? extends Serializable> entityClass, MongoCollection<Document> collection) {
        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }

        if (collection == null) {
            log.error("Collection can't be null");
            throw new IllegalArgumentException("Collection can't be null");
        }

        findAllAnnotations(Index.class, entityClass).stream().filter(index -> Objects.nonNull(index.value())) //
                                                    .filter(index -> index.value().isEmpty() == false) //
                                                    .forEach(index -> {
                                                        Document indexDocument = new Document();
                                                        indexDocument.put(index.value(), index.direction());

                                                        IndexOptions options = new IndexOptions();
                                                        options.unique(index.unique());
                                                        options.background(index.background());
                                                        collection.createIndex(indexDocument, options);
                                                    });

    }

    private <T extends Annotation> T findSingleAnnotation(Class<T> annotationClass, Class<?> clazz) {
        T foundAnnotation = clazz.getAnnotation(annotationClass);

        if (foundAnnotation != null) {
            return foundAnnotation;
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces == null) {
            return null;
        }
        List<T> annotations = Arrays.asList(interfaces).stream() //
                                    .map(iface -> findSingleAnnotation(annotationClass, iface)) //
                                    .filter(Objects::nonNull) //
                                    .collect(Collectors.toList());
        if (annotations.isEmpty()) {
            return null;
        }
        if (annotations.size() != 1) {
            throw new RuntimeException("Not more than one @Collection annotation is allowed to exists in the whole graph of an entity! Found: "
                                       + annotations.size());
        }

        return annotations.get(0);
    }

    private <T extends Annotation> List<T> findAllAnnotations(Class<T> annotationClass, Class<?> clazz) {

        List<T> allAnnotations = new ArrayList<>();

        // 1. Check for annotation in clazz (and inherited class)
        T[] foundAnnotations = clazz.getAnnotationsByType(annotationClass);

        if (foundAnnotations != null) {
            allAnnotations.addAll(Arrays.asList(foundAnnotations));
        }

        Class<?>[] interfaces = clazz.getInterfaces();
        if (interfaces != null) {
            List<T> annotations = Arrays.asList(interfaces).stream() //
                                        .map(iface -> findAllAnnotations(annotationClass, iface))
                                        .filter(Objects::nonNull) //
                                        .collect(ArrayList::new, List::addAll, List::addAll);
            allAnnotations.addAll(annotations);
        }

        return allAnnotations;
    }

}
