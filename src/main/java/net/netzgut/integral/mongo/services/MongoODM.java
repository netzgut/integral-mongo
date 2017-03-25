package net.netzgut.integral.mongo.services;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.bson.conversions.Bson;

import com.mongodb.client.model.CountOptions;
import com.mongodb.client.result.UpdateResult;

public interface MongoODM {

    <T extends Serializable> void persist(T entity);

    default <T extends Serializable> void persist(List<T> entities) {
        entities.forEach(this::persist);
    }

    <T extends Serializable> UpdateResult replace(Bson filter, T entity);

    <T extends Serializable> UpdateResult upsert(Bson filter, T entity);

    <T extends Serializable> UpdateResult update(Bson filter, T entity);

    <T extends Serializable> UpdateResult update(Bson filter, Class<T> entityClass, Map<String, Object> updateMap);

    <T extends Serializable> T findFirst(Bson filter, Class<T> entityClass);

    <T extends Serializable> Stream<T> find(Bson filter, Class<T> entityClass);

    <T extends Serializable> Stream<T> findAll(Class<T> entityClass);

    <T extends Serializable> long count(Class<T> entityClass);

    <T extends Serializable> long count(Bson filter, Class<T> entityClass);

    <T extends Serializable> long count(Bson filter, Class<T> entityClass, CountOptions options);

}
