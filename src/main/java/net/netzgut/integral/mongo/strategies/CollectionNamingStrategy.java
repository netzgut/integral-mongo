package net.netzgut.integral.mongo.strategies;

public interface CollectionNamingStrategy {

    String name(String originalName);
}
