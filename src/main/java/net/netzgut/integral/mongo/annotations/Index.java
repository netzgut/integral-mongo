package net.netzgut.integral.mongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation that can defines an entitity to have an MongoDB-index.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Repeatable(Indexes.class)
public @interface Index {

    /**
     * Helper interface for easier usage.
     */
    interface IndexDirection {

        int ASC  = 1;
        int DESC = -1;
    }

    /**
     * Field name of the index key.<br />
     * <br />
     * This needs to be the MongoDB-document field name, not the Java field name!
     */
    String value() default "";

    /**
     * Direction of the index.<br />
     * <br />
     * Default: {@link IndexDirection}.ASC
     */
    int direction() default IndexDirection.ASC;

    /**
     * Builds the index in the background, defaults to false.<br />
     * <br />
     * Default: false
     */
    boolean background() default false;

    /**
     * Creates an unique index.<br />
     * <br />
     * Default: false
     */
    boolean unique() default false;

}
