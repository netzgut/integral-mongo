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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mongodb.client.FindIterable;

public interface MongoConverter {

    /**
     * Converts a {@link org.bson.Document} to an entity.
     */
    <T extends Serializable> T entityFrom(Document document, Class<T> clazz);

    /**
     * Converts an entity to a {@link org.bson.Document}.
     */
    Document documentFrom(Serializable json);

    /**
     * Converts the content of an {@link java.lang.Iterable} to a List of entities.
     */
    default <T extends Serializable> List<T> entitiesFrom(FindIterable<Document> iterable, Class<T> entityClass) {
        if (iterable == null || entityClass == null) {
            return Collections.emptyList();
        }

        List<T> entities = new ArrayList<>();
        iterable.forEach((Consumer<Document>) (Document document) -> entities.add(this.entityFrom(document,
                                                                                                  entityClass)));

        return entities;
    }

    /**
     * Converts a List of entities to a List of {@link org.bson.Document}.
     */
    default List<Document> documentsFrom(List<? extends Serializable> data) {
        if (data == null || data.size() == 0) {
            return Collections.emptyList();
        }

        return data.stream().map(this::documentFrom).collect(Collectors.toList());
    }
}
