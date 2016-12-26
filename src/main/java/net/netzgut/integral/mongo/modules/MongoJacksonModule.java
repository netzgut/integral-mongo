package net.netzgut.integral.mongo.modules;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.ImportModule;

import net.netzgut.integral.mongo.internal.converter.JacksonConverterImplementation;
import net.netzgut.integral.mongo.internal.provider.ObjectMapperProviderDefaultImplementation;
import net.netzgut.integral.mongo.provider.ObjectMapperProvider;
import net.netzgut.integral.mongo.services.MongoConverter;

@ImportModule({ MongoModule.class })
public class MongoJacksonModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(MongoConverter.class, JacksonConverterImplementation.class);
        binder.bind(ObjectMapperProvider.class, ObjectMapperProviderDefaultImplementation.class);
    }
}
