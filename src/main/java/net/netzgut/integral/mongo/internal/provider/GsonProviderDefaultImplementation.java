package net.netzgut.integral.mongo.internal.provider;

import com.google.gson.Gson;

import net.netzgut.integral.mongo.provider.GsonProvider;

public class GsonProviderDefaultImplementation implements GsonProvider {

    @Override
    public Gson provide() {
        return new Gson();
    }

}
