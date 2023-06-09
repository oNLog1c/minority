package me.nologic.minority;

import lombok.SneakyThrows;
import me.nologic.minority.annotations.Configurable;
import me.nologic.minority.annotations.ConfigurationKey;
import me.nologic.minority.annotations.Translatable;
import me.nologic.minority.annotations.TranslationKey;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Objects;

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
                    field.set(object, plugin.getLanguage().get(path));
                }
            }
        }

        // 2. If class have @Configurable annotation, look for fields which annotated with @ConfigurationKey
        if (clazz.isAnnotationPresent(Configurable.class)) {
            final Configurable section = clazz.getAnnotation(Configurable.class);
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(ConfigurationKey.class)) {
                    final ConfigurationKey key = field.getAnnotation(ConfigurationKey.class);

                    final YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), section.file()));
                    String path = section.path() + config.options().pathSeparator() + key.name();
                    switch (key.type()) {
                        case STRING -> field.set(object, config.getString(path));
                        case BOOLEAN -> field.setBoolean(object, config.getBoolean(path));
                        case INTEGER -> field.setInt(object, config.getInt(path));
                        case DOUBLE -> field.setDouble(object, config.getDouble(path));
                        case ENUM -> field.set(object, Enum.valueOf(field.getType().asSubclass(Enum.class), Objects.requireNonNull(config.getString(path)))); // enum used for automatic cast, for example if we're using Material or EntityType as fields
                    }
                }
            }
        }

    }

}