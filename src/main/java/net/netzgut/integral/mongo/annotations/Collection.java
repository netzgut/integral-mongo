package net.netzgut.integral.mongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can defines an enitity is needing/having a MongoDB-collection.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Collection {

    /**
     * <p>The collection name.</p>
     * <p>If no value is provided the simple class name of the annotated type will be used.</p>
     */
    String value() default "";

    /**
     * Mark the class as autoSetup-able.
     */
    boolean autoSetup() default true;

}
