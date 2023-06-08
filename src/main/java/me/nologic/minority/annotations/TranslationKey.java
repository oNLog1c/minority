package me.nologic.minority.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.FIELD)
public @interface TranslationKey {

    String section() default "default";

    String name();

    /* Default value. */
    String value();

    // TODO: add support to list of strings

    String[] comment() default {};

}