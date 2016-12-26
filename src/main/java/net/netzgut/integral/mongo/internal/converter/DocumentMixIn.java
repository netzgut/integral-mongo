package net.netzgut.integral.mongo.internal.converter;

import com.fasterxml.jackson.annotation.JsonAnySetter;

public interface DocumentMixIn {

    @JsonAnySetter
    Object put(final String key, final Object value);

}
