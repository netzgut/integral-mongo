package net.netzgut.integral.mongo.internal.services;

import java.io.Serializable;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;

import net.netzgut.integral.mongo.services.MongoConverter;
import net.netzgut.integral.mongo.services.MongoPersister;
import net.netzgut.integral.mongo.services.MongoService;

public class MongoPersisterImplementation implements MongoPersister {

    private final MongoService   mongo;
    private final MongoConverter converter;

    public MongoPersisterImplementation(MongoService mongo, MongoConverter converter) {
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

}
