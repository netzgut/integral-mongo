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
