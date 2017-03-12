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
package net.netzgut.integral.mongo.internal;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import net.netzgut.integral.mongo.configuration.MongoConfigurationSymbols;
import net.netzgut.integral.mongo.internal.services.MongoPersisterImplementation;
import net.netzgut.integral.mongo.internal.services.MongoServiceImplementation;
import net.netzgut.integral.mongo.services.MongoPersister;
import net.netzgut.integral.mongo.services.MongoService;
import net.netzgut.integral.mongo.strategies.CollectionNamingStrategy;
import net.netzgut.integral.mongo.strategies.CollectionNamingStrategyDefaultImplementation;

public class MongoModule {

    @FactoryDefaults
    @Contribute(SymbolProvider.class)
    public static void supplyFactoryDefaults(MappedConfiguration<String, String> conf) {
        conf.add(MongoConfigurationSymbols.CONNECTION_HOST, "localhost");
        conf.add(MongoConfigurationSymbols.CONNECTION_PORT, "27017");
        conf.add(MongoConfigurationSymbols.AUTHENTICATION_DATABASE, "admin");
        conf.add(MongoConfigurationSymbols.AUTHENTICATION_USERNAME, "");
        conf.add(MongoConfigurationSymbols.AUTHENTICATION_PASSWORD, "");
    }

    public static void bind(ServiceBinder binder) {
        binder.bind(CollectionNamingStrategy.class, CollectionNamingStrategyDefaultImplementation.class);
        binder.bind(MongoService.class, MongoServiceImplementation.class);
        binder.bind(MongoPersister.class, MongoPersisterImplementation.class);
    }
}
