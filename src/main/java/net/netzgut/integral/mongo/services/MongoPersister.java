package net.netzgut.integral.mongo.services;

import java.io.Serializable;
import java.util.List;

import org.bson.conversions.Bson;

import com.mongodb.client.result.UpdateResult;

public interface MongoPersister {

    <T extends Serializable> void persist(T entity);

    default <T extends Serializable> void persist(List<T> entities) {
        entities.forEach(this::persist);
    }

    <T extends Serializable> UpdateResult replace(Bson filter, T entity);

    <T extends Serializable> UpdateResult upsert(Bson filter, T entity);

}