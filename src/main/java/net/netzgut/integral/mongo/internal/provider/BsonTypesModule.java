/**
 * Copyright 2018 Netzgut GmbH <info@netzgut.net>
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

import org.bson.types.Decimal128;

import com.fasterxml.jackson.databind.module.SimpleModule;

import net.netzgut.integral.mongo.internal.jackson.Decimal128Serializer;

public class BsonTypesModule extends SimpleModule {

    private static final long serialVersionUID = 1270743498976750809L;

    public BsonTypesModule() {
        super();

        addSerializer(Decimal128.class, new Decimal128Serializer());
    }

}
