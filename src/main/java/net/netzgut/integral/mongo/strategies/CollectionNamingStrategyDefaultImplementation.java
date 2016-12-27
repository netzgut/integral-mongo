package net.netzgut.integral.mongo.strategies;

public class CollectionNamingStrategyDefaultImplementation implements CollectionNamingStrategy {

    @Override
    public String name(String originalName) {
        return originalName;
    }

}
