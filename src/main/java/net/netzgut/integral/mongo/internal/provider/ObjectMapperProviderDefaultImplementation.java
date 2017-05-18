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
package net.netzgut.integral.mongo.internal.provider;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.netzgut.integral.mongo.internal.jackson.DocumentMixIn;
import net.netzgut.integral.mongo.provider.ObjectMapperProvider;

public class ObjectMapperProviderDefaultImplementation implements ObjectMapperProvider {

    private final ObjectMapper objectMapper;

    public ObjectMapperProviderDefaultImplementation() {
        this.objectMapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false) //
                                              .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true) //
                                              .setSerializationInclusion(Include.NON_NULL) //
                                              .addMixIn(Document.class, DocumentMixIn.class) //
                                              .registerModule(new JavaTimeModule()) //
                                              .registerModule(new Jdk8Module()) //
                                              .registerModule(new BsonTypesModule());
    }

    @Override
    public ObjectMapper provide() {
        return this.objectMapper;
    }

}
