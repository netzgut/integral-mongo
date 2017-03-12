package net.netzgut.integral.mongo.services;

import java.io.Serializable;

public interface MongoPersister {

    <T extends Serializable> void persist(T entity);
}
