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
package net.netzgut.integral.mongo.internal.converter;

import java.io.Serializable;

import org.bson.Document;

import com.google.gson.Gson;

import net.netzgut.integral.mongo.provider.GsonProvider;
import net.netzgut.integral.mongo.services.MongoConverter;

public class GsonConverterImplementation implements MongoConverter {

    private final Gson gson;

    public GsonConverterImplementation(GsonProvider gsonProvider) {
        this.gson = gsonProvider.provide();
    }

    @Override
    public <T extends Serializable> T entityFrom(Document document, Class<T> clazz) {

        if (document == null || clazz == null) {
            return null;
        }

        return this.gson.fromJson(document.toJson(), clazz);
    }

    @Override
    public Document documentFrom(Serializable data) {

        if (data == null) {
            return null;
        }

        String json = this.gson.toJson(data);

        return this.gson.fromJson(json, Document.class);
    }

}
