package net.netzgut.integral.mongo.modules;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.FactoryDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;

import net.netzgut.integral.mongo.configuration.MongoConfigurationSymbols;
import net.netzgut.integral.mongo.internal.services.MongoServiceImplementation;
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
    }
}
