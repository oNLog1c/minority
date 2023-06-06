package me.nologic.minority;

import lombok.SneakyThrows;
import me.nologic.minority.annotations.Key;
import me.nologic.minority.annotations.Translatable;

import java.lang.reflect.Field;

public interface MinorityFeature {

    /**
     * Automatic field initialisation (only for language ATM)
     * @param object is the object which will be initialized
     * @param clazz is a class which should implement MinorityFeature
     * */
    @SneakyThrows
    default void init(Object object, Class<? extends MinorityFeature> clazz, MinorityExtension plugin) {

        // 1. If class have @Translatable annotation, look for fields with @Key annotation and init it
        if (clazz.isAnnotationPresent(Translatable.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(Key.class)) {
                    final Key key = field.getAnnotation(Key.class);
                    final String path = key.section() + plugin.getLanguage().options().pathSeparator() + key.path();
                    field.setAccessible(true);
                    field.set(object, plugin.getLanguage().get(path));
                }
            }
        }

    }

}