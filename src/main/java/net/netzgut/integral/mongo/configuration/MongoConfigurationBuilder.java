package net.netzgut.integral.mongo.configuration;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;

public class MongoConfigurationBuilder {

    private String                      host;
    private int                         port;
    private String                      databaseName;
    private MongoClientOptions          options;

    private final List<MongoCredential> credentials = new ArrayList<>();

    /**
     * Creates a builder for {@link net.netzgut.integral.mongo.services.MongoServiceImplementation}
     */
    public MongoConfigurationBuilder() {
        // NOOP
    }

    /**
     * Set the host of the MongoDB server
     */
    public MongoConfigurationBuilder host(String host) {
        if (host == null || host.length() == 0) {
            throw new IllegalArgumentException("Host can't be blank.");
        }

        this.host = host;

        return this;
    }

    /**
     * Set the port of the MongoDB server
     */
    public MongoConfigurationBuilder port(int port) {
        if (port < 1 || port > 65335) {
            throw new IllegalArgumentException(String.format("Port must be between 1-65335 (actual: %d).", port));
        }

        this.port = port;

        return this;
    }

    /**
     * Set the host and port of the MongoDB server
     *
     * @param host The hostname or IP of the MongoDB server (required)
     * @param port The port of the MongoDB server (1-65335)
     * @return Builder object to allow for chaining of calls
     */
    public MongoConfigurationBuilder host(String host, int port) {
        return host(host).port(port);
    }

    /**
     * Set the default database
     *
     * @param defaultDatabase The default database used for many methods (required)
     * @return Builder object to allow for chaining of calls
     */
    public MongoConfigurationBuilder databaseName(String databaseName) {
        if (databaseName == null || databaseName.length() == 0) {
            throw new IllegalArgumentException("DatabaseName shouldn't be blank.");
        }

        this.databaseName = databaseName;

        return this;
    }

    /**
     * Adds a {@link com.mongodb.MongoCredential} to the list of credentials
     *
     * @param credential A {@link com.mongodb.MongoCredential} object (required)
     * @return Builder object to allow for chaining of calls
     */
    public MongoConfigurationBuilder addCredential(MongoCredential credential) {
        if (credential == null) {
            throw new IllegalArgumentException("Can't add null credentials.");
        }

        this.credentials.add(credential);

        return this;
    }

    /**
     * Adds a {@link com.mongodb.MongoCredential} to the list of credentials
     * based on the provided username/database/password
     */
    public MongoConfigurationBuilder addCredential(String username, String database, String password) {
        if (username == null || username.length() == 0) {
            throw new IllegalArgumentException("Can't add null credentials (username).");
        }

        if (database == null || database.length() == 0) {
            throw new IllegalArgumentException("Can't add null credentials (database).");
        }

        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());

        return addCredential(credential);
    }

    /**
     * Sets the {@link com.mongodb.MongoClientOptions}
     */
    public MongoConfigurationBuilder options(MongoClientOptions options) {
        this.options = options;

        return this;
    }

    /**
     * Builds a {@link net.netzgut.integral.mongo.servics.MongoService} instance
     * based on the provided arguments / with senseful fallbacks
     */
    public MongoConfiguration build() {
        return new MongoConfiguration() {

            @Override
            public ServerAddress getServerAddress() {
                return new ServerAddress(MongoConfigurationBuilder.this.host, MongoConfigurationBuilder.this.port);
            }

            @Override
            public String getDatabaseName() {
                return MongoConfigurationBuilder.this.databaseName;
            }

            @Override
            public List<MongoCredential> getCredentials() {
                return MongoConfigurationBuilder.this.credentials;
            }

            @Override
            public MongoClientOptions getClientOptions() {
                if (MongoConfigurationBuilder.this.options == null) {
                    options(MongoClientOptions.builder().build());
                }
                return MongoConfigurationBuilder.this.options;
            }
        };
    }
}
