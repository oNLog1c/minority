package me.nologic.minority.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
public @interface Key {

    String path();

    /* Default value. */
    String value();

    /* Type of the field. */
    Type type() default Type.STRING;

    String[] comment();

}