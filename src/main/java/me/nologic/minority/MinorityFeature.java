package me.nologic.minority;

import lombok.SneakyThrows;
import me.nologic.minority.annotations.Configurable;
import me.nologic.minority.annotations.ConfigurationKey;
import me.nologic.minority.annotations.Translatable;
import me.nologic.minority.annotations.TranslationKey;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;

public interface MinorityFeature {

    /**
     * Automatic field initialization.
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
                    field.setAccessible(true);

                    // 3. Because of the apostrophe, we have to parse the value ourselves
                    final String value = config.getString(path);
                    if (value != null) {
                        switch (key.type()) {
                            case STRING -> field.set(object, value);
                            case BOOLEAN -> field.setBoolean(object, Boolean.parseBoolean(value));
                            case INTEGER -> field.setInt(object, Integer.parseInt(value));
                            case DOUBLE -> field.setDouble(object, Double.parseDouble(value));
                            case FLOAT -> field.setFloat(object, Float.parseFloat(value));
                            case ENUM -> field.set(object, Enum.valueOf(field.getType().asSubclass(Enum.class), value)); // enum used for automatic cast, for example if we're using Material or EntityType as fields
                            case LIST_OF_STRINGS -> field.set(object, config.getStringList(path));
                        }
                    }

                }
            }
        }

    }

}