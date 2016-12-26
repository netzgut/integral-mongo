package net.netzgut.integral.mongo.internal.converter;

import java.io.Serializable;

import org.bson.Document;

import com.fasterxml.jackson.databind.ObjectMapper;

import net.netzgut.integral.mongo.provider.ObjectMapperProvider;
import net.netzgut.integral.mongo.services.MongoConverter;

public class JacksonConverterImplementation implements MongoConverter {

    private final ObjectMapper objectMapper;

    public JacksonConverterImplementation(ObjectMapperProvider objectMapperProvider) {
        this.objectMapper = objectMapperProvider.provide();
    }

    @Override
    public <T extends Serializable> T entityFrom(Document document, Class<T> clazz) {

        if (document == null || clazz == null) {
            return null;
        }

        return this.objectMapper.convertValue(document, clazz);
    }

    @Override
    public Document documentFrom(Serializable data) {

        if (data == null) {
            return null;
        }

        return this.objectMapper.convertValue(data, Document.class);
    }

}
