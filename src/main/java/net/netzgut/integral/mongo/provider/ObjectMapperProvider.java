package net.netzgut.integral.mongo.provider;

import com.fasterxml.jackson.databind.ObjectMapper;

public interface ObjectMapperProvider {

    ObjectMapper provide();
}
