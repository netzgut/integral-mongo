package net.netzgut.integral.mongo.internal.jackson;

import java.io.IOException;

import org.bson.types.Decimal128;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class Decimal128Serializer extends JsonSerializer<Decimal128> {

    @Override
    public void serialize(Decimal128 value, JsonGenerator gen, SerializerProvider serializers) throws IOException,
                                                                                               JsonProcessingException {
        if (value == null) {
            gen.writeNull();
            return;
        }

        gen.writeNumber(value.bigDecimalValue());
    }

}
