package me.nologic.minority.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nologic.minority.MinorityExtension;
import me.nologic.minority.MinorityFeature;
import me.nologic.minority.annotations.ConfigurationKey;
import me.nologic.minority.annotations.Configurable;

import me.nologic.minority.annotations.Translatable;
import me.nologic.minority.annotations.TranslationKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ConfigurationWizard {

    private final MinorityExtension plugin;

    /**
     * Generate translation file and/or configuration file for specified class which instanceof MinorityFeature
     * and have some of these annotations: @Configurable or @Translatable.
     * @param feature a class that extends MinorityFeature
     */
    @SneakyThrows
    public final void generate(Class<? extends MinorityFeature> feature) {

        // If class have @Translatable annotation, generate translate file for it
        if (feature.isAnnotationPresent(Translatable.class)) {
            this.generateTranslation(feature);
        }

        // If class have @Configurable annotation, generate config file for it
        if (feature.isAnnotationPresent(Configurable.class)) {
            this.generateConfiguration(feature);
        }

    }

    /**
     * Automatically used on classes with @Configurable annotation, looking for @ConfigurationKey.
     * Creates a new config file if it not exist, or update the existing one with the missing keys.
     * */
    @SneakyThrows
    private void generateConfiguration(final Class<? extends MinorityFeature> feature) {

        // 1. Firstly, we create a new YamlConfiguration.
        final YamlConfiguration config = new YamlConfiguration();

        // TODO: Section.class have the version field. It should be used for auto-updating configurations which is outdated.
        final Configurable section = feature.getAnnotation(Configurable.class);
        final File file = new File(plugin.getDataFolder(), section.file());

        // 2. Then, try to load existing configuration. Otherwise, it will be created from scratch.
        if (file.exists()) config.load(file);

        // 3. Add header to the config file. TODO: Make it configurable. Or not?
        config.options().setHeader(Collections.singletonList("This configuration file was automatically generated with Minority."));

        // 3.1 Add comments to the section (if there any comments)
        if (section.comment().length > 0) {
            config.setComments(section.path(), List.of(section.comment()));
        }

        // 4. Scan all fields in iterable class, if field have annotation @ConfigurationKey, it will be stored in our config.
        for (Field field : feature.getDeclaredFields()) {
            if (field.isAnnotationPresent(ConfigurationKey.class)) {
                final ConfigurationKey key = field.getAnnotation(ConfigurationKey.class);

                field.setAccessible(true);

                ConfigurationSection s = config.getConfigurationSection(section.path()) == null ? config.createSection(section.path()) : config.getConfigurationSection(section.path());
                if (s != null) {
                    // 4.1 We should add and assign keys only if there are no path for it.
                    if (!s.contains(key.name())) {
                        s.set(key.name(), key.value());
                        s.setComments(key.name(), List.of(key.comment())); // Add comments to path in our config.
                    }
                }

            }
        }

        // 5. And after all the job is done, we shouldn't forget to save the config file!
        config.save(file);
    }

    /**
     * Automatically used on classes with @Translatable annotation, looking for @TranslationKey.
     * Creates a new language file if it not exist, or update the existing one with the missing keys.
     * */
    @SneakyThrows
    private void generateTranslation(final Class<? extends MinorityFeature> translatable) {

        final YamlConfiguration language = new YamlConfiguration();

        // 1. Firstly, we want to check is the language file exist.
        Translatable tongue = translatable.getAnnotation(Translatable.class);
        File file = new File(this.plugin.getLanguageFolder(), tongue.file());

        // 2. If language file already exist, load it to our yaml configuration.
        if (file.exists()) language.load(file);

        // 3. Add header to the language file. TODO: Make it configurable. Or not?
        language.options().setHeader(Collections.singletonList("This language file was automatically generated with Minority."));

        // 4. We want to scan all fields that marked with @TranslationKey annotation, then read its path and value.
        for (Field field : translatable.getDeclaredFields()) {
            if (field.isAnnotationPresent(TranslationKey.class)) {
                TranslationKey key = field.getAnnotation(TranslationKey.class);

                // 4.1 If path is already exists, we change nothing, otherwise we save the default value.
                String path = key.section() + language.options().pathSeparator() + key.name();
                if (!language.contains(path)) {
                    language.set(path, key.value());
                }
            }
        }

        // 5. Generation of the language file completed, now we need to save it.
        language.save(file);

    }

}