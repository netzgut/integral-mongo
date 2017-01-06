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
package net.netzgut.integral.mongo.modules;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

import net.netzgut.integral.mongo.internal.MongoModule;
import net.netzgut.integral.mongo.internal.converter.GsonConverterImplementation;
import net.netzgut.integral.mongo.internal.provider.GsonProviderDefaultImplementation;
import net.netzgut.integral.mongo.provider.GsonProvider;
import net.netzgut.integral.mongo.services.MongoConverter;

@ImportModule({ MongoModule.class })
public class MongoGsonModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(GsonProvider.class, GsonProviderDefaultImplementation.class);
        binder.bind(MongoConverter.class, GsonConverterImplementation.class);
    }
}
