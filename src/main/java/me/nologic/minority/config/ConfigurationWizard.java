package me.nologic.minority.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import me.nologic.minority.MinorityExtension;
import me.nologic.minority.MinorityFeature;
import me.nologic.minority.annotations.Key;
import me.nologic.minority.annotations.Section;

import me.nologic.minority.annotations.Translatable;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
public class ConfigurationWizard {

    private final MinorityExtension plugin;

    @SneakyThrows
    public final void generate(Class<? extends MinorityFeature> feature) {

        // 0. Checking for translation file.
        if (feature.isAnnotationPresent(Translatable.class)) {
            this.translate(feature);
            return;
        }

        // 1. Firstly, we create a new YamlConfiguration.
        final YamlConfiguration config = new YamlConfiguration();

        // TODO: Section.class have the version field. It should be used for auto-updating configurations which is outdated.
        final Section section = feature.getAnnotation(Section.class);
        final File file = new File(plugin.getDataFolder(), section.file());

        // 2. Then, try to load existing configuration. Otherwise, it will be created from scratch.
        if (file.exists()) config.load(file);

        // 3. Add header to the config file. TODO: Make it configurable. Or not?
        config.options().setHeader(Collections.singletonList("This configuration file was automatically generated with Minority."));

        // 4. Scan all fields in iterable class, if field have annotation @Key, it will be stored in our config.
        for (Field field : feature.getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                final Key key = field.getAnnotation(Key.class);

                field.setAccessible(true);

                ConfigurationSection s = config.getConfigurationSection(section.path()) == null ? config.createSection(section.path()) : config.getConfigurationSection(section.path());
                if (s != null) {
                    // 4.1 We should add and assign keys only if there are no path for it.
                    if (!s.contains(key.path())) {
                        s.set(key.path(), key.value());
                        s.setComments(key.path(), List.of(key.comment())); // Add comments to path in our config.
                    }
                }

            }
        }

        // TODO: Separate instance creation and event handler registration in other method. It shouldn't be automatic.
        // 5. Now, it's time to create a new instance of our class and assign all fields that have the @Key annotation with values from generated/loaded configuration.
        MinorityFeature instance = feature.getConstructor().newInstance();
        for (Field field : instance.getClass().getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                Key key = field.getAnnotation(Key.class);

                // TODO: Add support to arrays.
                // Parse the value and assign it to the marked field!
                // Very important to read the values from a newly generated config, not from an annotation above!
                // Also, we can skip checks for null pointers, because we ALWAYS have the config with the paths that we're need.
                String path = section.path() + config.options().pathSeparator() + key.path();
                switch (key.type()) {
                    case STRING -> field.set(instance, config.getString(path));
                    case BOOLEAN -> field.setBoolean(instance, config.getBoolean(path));
                    case INTEGER -> field.setInt(instance, config.getInt(path));
                    case DOUBLE -> field.setDouble(instance, config.getDouble(path));
                    case ENUM -> field.set(instance, Enum.valueOf(field.getType().asSubclass(Enum.class), Objects.requireNonNull(config.getString(path)))); // enum used for automatic cast, for example if we're using Material or EntityType as fields
                }
            }
        }

        // 6. Right after that, we try to find out is our class have @EventHandler. If true, it will be considered as Listener.
        boolean isListener = false;
        for (Method method : instance.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                plugin.getLogger().info("Listener detected: " + instance.getClass());
                isListener = true;
            }
        }

        // 7. If iterating class is Listener, we should automatically register it in Bukkit plugin manager.
        if (isListener) {
            plugin.getLogger().info("Detected the @EventHandler annotation usage in the " + instance.getClass().getName() + "class. Registering it as Listener.");
            plugin.getServer().getPluginManager().registerEvents((Listener) instance, plugin);
        }

        // 9. And after all the job is done, we shouldn't forget to save the config file!
        config.save(file);

    }

    /**
     * Automatically used on classes with @Translatable annotation.
     * Creates a new language file if it not exist, or update the existing one with the missing keys.
     * */
    @SneakyThrows
    private void translate(Class<? extends MinorityFeature> translatable) {

        final YamlConfiguration language = new YamlConfiguration();

        // 1. Firstly, we want to check is the language file exist.
        Translatable tongue = translatable.getAnnotation(Translatable.class);
        File file = new File(this.plugin.getLanguageFolder(), tongue.file());

        // 2. If language file already exist, load it to our yaml configuration.
        if (file.exists()) language.load(file);

        // 3. Add header to the language file. TODO: Make it configurable. Or not?
        language.options().setHeader(Collections.singletonList("This language file was automatically generated with Minority."));

        // 4. We want to scan all fields that marked with @Key annotation, then read its path and value.
        for (Field field : translatable.getDeclaredFields()) {
            if (field.isAnnotationPresent(Key.class)) {
                Key key = field.getAnnotation(Key.class);

                // 4.1 If path is already exists, we change nothing, otherwise we save the default value.
                String path = key.section() + language.options().pathSeparator() + key.path();
                if (!language.contains(path)) {
                    language.set(path, key.value());
                }
            }
        }

        // 5. Generation of the language file completed, now we need to save it.
        language.save(file);

    }

}