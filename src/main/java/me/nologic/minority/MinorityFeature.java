package me.nologic.minority;

import lombok.SneakyThrows;
import me.nologic.minority.annotations.Translatable;
import me.nologic.minority.annotations.TranslationKey;

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
                if (field.isAnnotationPresent(TranslationKey.class)) {
                    final TranslationKey key = field.getAnnotation(TranslationKey.class);
                    final String path = key.section() + plugin.getLanguage().options().pathSeparator() + key.name();
                    field.setAccessible(true);
                    plugin.getLogger().info(String.format("Trying to change field %s value to %s.", field.getName(), plugin.getLanguage().get(path)));
                    field.set(object, plugin.getLanguage().get(path));
                    plugin.getLogger().info(String.format("New value in field %s: %s.", field.getName(), field.get(object)));

                }
            }
        }

    }

}