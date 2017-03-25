package net.netzgut.integral.mongo.internal.services;

import java.io.Serializable;
import java.util.Map;
import java.util.stream.Stream;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.CountOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import net.netzgut.integral.mongo.services.MongoConverter;
import net.netzgut.integral.mongo.services.MongoODM;
import net.netzgut.integral.mongo.services.MongoService;

public class MongoODMImplementation implements MongoODM {

    private final MongoService   mongo;
    private final MongoConverter converter;

    public MongoODMImplementation(MongoService mongo, MongoConverter converter) {
        this.mongo = mongo;
        this.converter = converter;
    }

    @Override
    public <T extends Serializable> void persist(T entity) {
        Class<? extends Serializable> entityClass = entity.getClass();
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Document document = this.converter.documentFrom(entity);
        collection.insertOne(document);
    }

    @Override
    public <T extends Serializable> UpdateResult replace(Bson filter, T entity) {
        Class<? extends Serializable> entityClass = entity.getClass();
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Document replacement = this.converter.documentFrom(entity);
        return collection.replaceOne(filter, replacement);
    }

    @Override
    public <T extends Serializable> UpdateResult upsert(Bson filter, T entity) {
        Class<? extends Serializable> entityClass = entity.getClass();
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Document document = this.converter.documentFrom(entity);
        Bson update = Filters.eq("$set", document);
        return collection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

    @Override
    public <T extends Serializable> UpdateResult update(Bson filter, T entity) {
        Class<? extends Serializable> entityClass = entity.getClass();
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Document document = this.converter.documentFrom(entity);
        Bson update = Filters.eq("$set", document);
        return collection.updateOne(filter, update);
    }

    @Override
    public <T extends Serializable> UpdateResult update(Bson filter,
                                                        Class<T> entityClass,
                                                        Map<String, Object> updateMap) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Bson update = Filters.eq("$set", new Document(updateMap));
        return collection.updateOne(filter, update);
    }

    @Override
    public <T extends Serializable> Stream<T> find(Bson filter, Class<T> entityClass) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        FindIterable<Document> find = collection.find(filter);
        return this.converter.entitiesStreamFrom(find, entityClass);
    }

    @Override
    public <T extends Serializable> T findFirst(Bson filter, Class<T> entityClass) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        Document document = collection.find(filter).limit(1).first();
        if (document == null) {
            return null;
        }
        return this.converter.entityFrom(document, entityClass);
    }

    @Override
    public <T extends Serializable> Stream<T> findAll(Class<T> entityClass) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        FindIterable<Document> find = collection.find();
        return this.converter.entitiesStreamFrom(find, entityClass);
    }

    @Override
    public <T extends Serializable> long count(Class<T> entityClass) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        return collection.count();
    }

    @Override
    public <T extends Serializable> long count(Bson filter, Class<T> entityClass) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        return collection.count(filter);
    }

    @Override
    public <T extends Serializable> long count(Bson filter, Class<T> entityClass, CountOptions options) {
        MongoCollection<Document> collection = this.mongo.getCollection(entityClass);
        return collection.count(filter, options);
    }

}
