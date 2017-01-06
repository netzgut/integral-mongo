# ∫ Integral Mongo

A wrapper around the Mongo DB driver with a nice configuration builder and autosetup. Entities will be automatically converted by either Jackson or GSON.

## Usage

NOTE: This library isn't released yet on jcenter etc.!

### `build.gradle`:
```groovy
respositories {
  jcenter()
}

dependencies {
    compile "net.netzgut.integral:integral-mongo:0.0.1"
}
```

Now you need to either import `net.netzgut.integral.mongo.modules.MongoGsonModule` or `net.netzgut.integral.mongo.modules.MongoJacksonModule`


### Configuration

Provide a builder-method for `net.netzgut.integral.mongo.configuration.MongoConfiguration`:

```Java
public static MongoConfiguration buildMongoConfiguration(@Inject @Symbol(MongoConfigurationSymbols.CONNECTION_HOST) String host,
                                                         @Inject @Symbol(MongoConfigurationSymbols.CONNECTION_PORT) int port,
                                                         @Inject @Symbol(MongoConfigurationSymbols.DATABASE_NAME) String databaseName,
                                                         @Inject @Symbol(MongoConfigurationSymbols.AUTHENTICATION_DATABASE) String authDb,
                                                         @Inject @Symbol(MongoConfigurationSymbols.AUTHENTICATION_USERNAME) String username,
                                                         @Inject @Symbol(MongoConfigurationSymbols.AUTHENTICATION_PASSWORD) String password) {

        MongoConfigurationBuilder builder = new MongoConfigurationBuilder().host(host) //
                                                                           .port(port) //
                                                                           .databaseName(databaseName);

        if (username.length() != 0) {
            builder.addCredential(username, authDb, password);
        }

        return builder.build();
    }
```


### Accessing databases  / collections

Just inject `net.netzgut.integral.mongo.services.MongoService` and use it.

You can also access the collections for your entities by the actual Type, the service
will derive the name from the Type or a `net.netzgut.integral.mongo.annotations.Collection`
annotation.


### (Optional) CollectionNamingStrategy

Normally you will just get the collection by the name you requested i, but we use MongoDB in a tenant-based system,
so it would be nice to access the correct collection but still use a single collection name.

You can override the service `net.netzgut.integral.mongo.strategies.CollectionNamingStrategy` to modify the collection
name everytime `MongoService` accesses it.


### (Optional) Auto Setup

Annotation-based auto-setup of collections.

TBD


### Converting entities

The `net.netzgut.integral.mongo.services.MongoConverter` is used to convert entities to MongoDB documents
and vice-versa.


## Contribute

It's awesome that you want to contribute! Please see [this repository](https://github.com/netzgut/contribute)
for more details.



## License

Apache 2.0 license, see `LICENSE.txt` and `NOTICE.txt` for more details.
