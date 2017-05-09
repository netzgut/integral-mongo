package net.netzgut.integral.mongo.codecs;

import java.math.BigDecimal;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;
import org.bson.types.Decimal128;

/**
 * Codec for {@link java.math.BigDecimal}.
 * Needs MongoDB 3.4 to work due to needing Decimal128-support.
 */
public class BigDecimalDecimal128Codec implements Codec<BigDecimal> {

    @Override
    public Class<BigDecimal> getEncoderClass() {
        return BigDecimal.class;
    }

    @Override
    public void encode(BsonWriter writer, BigDecimal value, EncoderContext encoderContext) {
        writer.writeDecimal128(new Decimal128(value));
    }

    @Override
    public BigDecimal decode(BsonReader reader, DecoderContext decoderContext) {
        return reader.readDecimal128().bigDecimalValue();
    }
}
