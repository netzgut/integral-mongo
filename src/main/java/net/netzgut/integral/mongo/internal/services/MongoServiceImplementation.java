package net.netzgut.integral.mongo.internal.services;

import java.io.Closeable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.bson.Document;
import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.IndexOptions;

import net.netzgut.integral.mongo.annotations.Collection;
import net.netzgut.integral.mongo.annotations.Index;
import net.netzgut.integral.mongo.configuration.MongoConfiguration;
import net.netzgut.integral.mongo.services.MongoService;

public class MongoServiceImplementation implements MongoService, Closeable {

    private static final Logger      log = LoggerFactory.getLogger(MongoServiceImplementation.class);

    private final MongoConfiguration configuration;

    private final MongoClient        mongoClient;
    private final MongoDatabase      defaultDatabase;

    public MongoServiceImplementation(MongoConfiguration configuration) {
        this.configuration = configuration;
        this.mongoClient = new MongoClient(configuration.getServerAddress(),
                                           configuration.getCredentials(),
                                           configuration.getClientOptions());
        this.defaultDatabase = this.mongoClient.getDatabase(configuration.getDatabaseName());
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
    public String getCollectionName(Class<?> entityClass) {
        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }

        Collection annotation = entityClass.getAnnotation(Collection.class);

        if (annotation == null) {
            String message = String.format("Annotation '@Collection' not present on class '%s'", entityClass.getName());
            MongoServiceImplementation.log.error(message);
            throw new UnsupportedOperationException(message);
        }

        String collectionName = annotation.value();
        if (collectionName == null || collectionName.length() == 0) {
            collectionName = entityClass.getName();
        }

        return getCollectionName(collectionName);
    }

    @Override
    public String getCollectionName(String collectionName) {
        return this.configuration.getCollectionNamingStrategy().name(collectionName);
    }

    @Override
    public MongoCollection<Document> getCollection(MongoDatabase db, String collectionName) {
        if (db == null) {
            log.error("Database can't be null");
            throw new IllegalArgumentException("Database can't be null");
        }

        if (collectionName == null || collectionName.length() == 0) {
            log.error("Collection name can't be blank");
            throw new IllegalArgumentException("Collection name can't be blank");
        }

        String finalCollectionName = getCollectionName(collectionName);

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
    public void setupCollection(MongoDatabase db, Class<?> entityClass) {
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
    public void setupCollection(MongoDatabase db, Class<?> entityClass, String collectionName) {
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

    @Override
    public void autoSetup(MongoDatabase db, String packageName) {
        MongoServiceImplementation.log.debug("autoSetup(DB: {}, Package: {}", db.getName(), packageName);

        List<ClassLoader> classLoadersList = new ArrayList<>();
        ClassLoader contextClassLoader = ClasspathHelper.contextClassLoader();
        classLoadersList.add(contextClassLoader);

        ClassLoader staticClassLoader = ClasspathHelper.staticClassLoader();
        if (staticClassLoader != contextClassLoader) {
            classLoadersList.add(staticClassLoader);
        }

        ConfigurationBuilder configuration =
            new ConfigurationBuilder().setScanners(new SubTypesScanner(false), new TypeAnnotationsScanner())
                                      .addClassLoaders(classLoadersList)
                                      .setUrls(ClasspathHelper.forPackage(packageName));
        Reflections reflections = new Reflections(configuration);

        Set<Class<?>> collections = reflections.getTypesAnnotatedWith(Collection.class);
        collections.forEach(collection -> {
            Collection annotation = collection.getAnnotation(Collection.class);
            if (annotation.autoSetup()) {
                this.setupCollection(db, collection, annotation.value());
            }
        });
    }

    @Override
    public void close() throws IOException {
        this.mongoClient.close();
    }

    private void setupIndexes(Class<?> entityClass, MongoCollection<Document> collection) {
        if (entityClass == null) {
            log.error("Entity Class can't be null");
            throw new IllegalArgumentException("Entity Class can't be null");
        }

        if (collection == null) {
            log.error("Collection can't be null");
            throw new IllegalArgumentException("Collection can't be null");
        }

        Index[] indexes = entityClass.getAnnotationsByType(Index.class);
        if (indexes != null) {
            Stream.of(indexes) //
                  .filter(index -> index.value() != null) //
                  .filter(index -> index.value().length() > 0) //
                  .forEach(index -> {
                      Document indexDocument = new Document();
                      indexDocument.put(index.value(), index.direction());

                      IndexOptions options = new IndexOptions();
                      options.unique(index.unique());
                      options.background(index.background());
                      collection.createIndex(indexDocument, options);
                  });
        }
    }

}
