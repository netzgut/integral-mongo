package net.netzgut.integral.mongo.internal.converter;

import java.io.Serializable;

import org.bson.Document;

import com.google.gson.Gson;

import net.netzgut.integral.mongo.provider.GsonProvider;
import net.netzgut.integral.mongo.services.MongoConverter;

public class GsonConverterImplementation implements MongoConverter {

    private final Gson gson;

    public GsonConverterImplementation(GsonProvider gsonProvider) {
        this.gson = gsonProvider.provide();
    }

    @Override
    public <T extends Serializable> T entityFrom(Document document, Class<T> clazz) {

        if (document == null || clazz == null) {
            return null;
        }

        return this.gson.fromJson(document.toJson(), clazz);
    }

    @Override
    public Document documentFrom(Serializable data) {

        if (data == null) {
            return null;
        }

        String json = this.gson.toJson(data);

        return this.gson.fromJson(json, Document.class);
    }

}
