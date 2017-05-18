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
