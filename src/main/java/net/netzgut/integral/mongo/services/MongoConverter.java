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
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.bson.Document;

import com.mongodb.client.MongoIterable;

public interface MongoConverter {

    /**
     * Converts a {@link org.bson.Document} to an entity.
     */
    <T extends Serializable> T entityFrom(Document document, Class<T> entityClass);

    /**
     * Converts an entity to a {@link org.bson.Document}.
     */
    Document documentFrom(Serializable entity);

    /**
     * Converts the content of an {@link java.lang.Iterable} to a List of entities.
     */
    default <T extends Serializable> List<T> entitiesFrom(MongoIterable<Document> iterable, Class<T> entityClass) {
        return entitiesStreamFrom(iterable, entityClass).collect(Collectors.toList());
    }

    /**
     * Provides a {@link Stream} to the entities.
     */
    default <T extends Serializable> Stream<T> entitiesStreamFrom(MongoIterable<Document> iterable,
                                                                  Class<T> entityClass) {
        Objects.requireNonNull(iterable, "Iterable musn't be null");
        Objects.requireNonNull(entityClass, "Entity Class musn't be null");

        return StreamSupport.stream(iterable.spliterator(), false)
                            .map(document -> this.entityFrom(document, entityClass));
    }

    /**
     * Converts a List of entities to a List of {@link org.bson.Document}.
     */
    default List<Document> documentsFrom(List<? extends Serializable> entities) {
        return documentsStreamFrom(entities).collect(Collectors.toList());
    }

    /**
     * Provides a {@link Stream} to the documents.
     */
    default Stream<Document> documentsStreamFrom(List<? extends Serializable> entities) {
        Objects.requireNonNull(entities, "Entities musn't be null");

        return entities.stream().map(this::documentFrom);
    }
}
