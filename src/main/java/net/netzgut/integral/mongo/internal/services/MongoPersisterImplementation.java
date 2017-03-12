package net.netzgut.integral.mongo.internal.services;

import java.io.Serializable;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

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

}
