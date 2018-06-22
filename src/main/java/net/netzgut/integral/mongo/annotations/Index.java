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
package net.netzgut.integral.mongo.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
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
@Inherited
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
