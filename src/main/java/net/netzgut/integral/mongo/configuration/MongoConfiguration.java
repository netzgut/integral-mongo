package net.netzgut.integral.mongo.configuration;

import java.util.List;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

/**
 * Simple wrapper for the configuration needed to create a {@link net.netzgut.integral.mongo.services.MongoService}.
 */
public interface MongoConfiguration {

    ServerAddress getServerAddress();

    String getDatabaseName();

    MongoClientOptions getClientOptions();

    List<MongoCredential> getCredentials();

}
