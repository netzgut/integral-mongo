package net.netzgut.integral.mongo.internal.provider;

import org.bson.Document;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import net.netzgut.integral.mongo.internal.converter.DocumentMixIn;
import net.netzgut.integral.mongo.provider.ObjectMapperProvider;

public class ObjectMapperProviderDefaultImplementation implements ObjectMapperProvider {

    @Override
    public ObjectMapper provide() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, true);
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.addMixIn(Document.class, DocumentMixIn.class);
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper;
    }

}
