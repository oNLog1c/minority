package me.nologic.minority.annotations;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME) @Target(ElementType.TYPE)
public @interface Configurable {

    /** Targeted config file, where section will be stored. By default, is config.yml. */
    String file() default "config.yml";

    /* Name of the section in the config file. */
    String path();

    /* Comment, which will be displayed in config file. */
    String[] comment() default {};

}