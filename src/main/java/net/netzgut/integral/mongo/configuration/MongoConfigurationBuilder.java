/**
 * Copyright 2018 Netzgut GmbH <info@netzgut.net>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.netzgut.integral.mongo.configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bson.codecs.Codec;
import org.bson.codecs.configuration.CodecRegistries;
import org.bson.codecs.configuration.CodecRegistry;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.event.ServerMonitorListener;

public class MongoConfigurationBuilder {

    private String                            host;
    private int                               port;
    private String                            databaseName;
    private MongoClientOptions                options;
    private final List<ServerMonitorListener> serverMonitorListeners = new ArrayList<>();
    private final List<MongoCredential>       credentials            = new ArrayList<>();
    private final List<Codec<?>>              codecs                 = new ArrayList<>();

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
        if (host == null || host.isEmpty()) {
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
        if (databaseName == null || databaseName.isEmpty()) {
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
     * Adds a {@link org.bson.codecs.configuration.Codec} to be added to the codec
     * registry if no options are provided.
     */
    public MongoConfigurationBuilder addCodec(Codec<?> codec) {
        if (codec == null) {
            throw new IllegalArgumentException("Can't add null codec.");
        }

        if (this.options != null) {
            throw new IllegalStateException("MongoClient options already set. "
                                            + "A Codec has to be included into the options "
                                            + "so either add it to your provided options or add a Codec "
                                            + "and use the default MongoClientOptions this builder will build.");
        }

        this.codecs.add(codec);

        return this;
    }

    /**
     * Adds a {@link com.mongodb.MongoCredential} to the list of credentials
     * based on the provided username/database/password.
     */
    public MongoConfigurationBuilder addCredential(String username, String database, String password) {
        if (username == null || username.isEmpty()) {
            throw new IllegalArgumentException("Can't add null credentials (username).");
        }

        if (database == null || database.isEmpty()) {
            throw new IllegalArgumentException("Can't add null credentials (database).");
        }

        MongoCredential credential = MongoCredential.createCredential(username, database, password.toCharArray());

        return addCredential(credential);
    }

    /**
     * Sets the {@link com.mongodb.MongoClientOptions}.
     */
    public MongoConfigurationBuilder options(MongoClientOptions options) {
        this.options = options;

        return this;
    }

    /**
     * Attach {@link com.mongodb.event.ServerMonitorListener}.
     * You can build a ServerMonitorListener with {@link net.netzgut.integral.mongo.configuration.ServerMonitorBuilder}.
     */
    public MongoConfigurationBuilder serverMonitor(ServerMonitorListener... monitors) {
        if (this.options != null) {
            throw new IllegalStateException("MongoClient options already set. "
                                            + "The ServerMonitorListener has to be included into the options "
                                            + "so either add it to your provided options or set the monitor "
                                            + "and use the default MongoClientOptions this builder will build.");
        }

        this.serverMonitorListeners.addAll(Arrays.asList(monitors));

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

                    Builder builder = MongoClientOptions.builder();
                    if (MongoConfigurationBuilder.this.serverMonitorListeners.isEmpty() == false) {
                        MongoConfigurationBuilder.this.serverMonitorListeners.forEach(builder::addServerMonitorListener);
                    }

                    if (MongoConfigurationBuilder.this.codecs.isEmpty() == false) {
                        CodecRegistry customRegistry =
                            CodecRegistries.fromCodecs(MongoConfigurationBuilder.this.codecs);
                        CodecRegistry defaultRegistry = MongoClient.getDefaultCodecRegistry();
                        CodecRegistry combinedRegistry =
                            CodecRegistries.fromRegistries(customRegistry, defaultRegistry);
                        builder.codecRegistry(combinedRegistry);
                    }

                    options(builder.build());
                }

                return MongoConfigurationBuilder.this.options;
            }
        };
    }
}
