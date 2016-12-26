package net.netzgut.integral.mongo.modules;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

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
