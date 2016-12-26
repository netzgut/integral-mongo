package net.netzgut.integral.mongo.modules;

import org.apache.tapestry5.ioc.ServiceBinder;

import net.netzgut.integral.mongo.internal.services.MongoServiceImplementation;
import net.netzgut.integral.mongo.services.MongoService;

public class MongoModule {

    public static void bind(ServiceBinder binder) {
        binder.bind(MongoService.class, MongoServiceImplementation.class).withId("asdas");
    }
}
