package me.nologic.minority;

import lombok.SneakyThrows;
import me.nologic.minority.config.ConfigurationWizard;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public abstract class MinorityExtension extends JavaPlugin {


    // TODO: add comments
    private ConfigurationWizard configurationWizard;
    private YamlConfiguration   language;

    public ConfigurationWizard getConfigurationWizard() {
        return configurationWizard == null ? (configurationWizard = new ConfigurationWizard(this)) : this.configurationWizard;
    }

    public File getLanguageFolder() {
        return new File(super.getDataFolder(), "languages");
    }

    @SneakyThrows
    public YamlConfiguration getLanguage() {

        final FileConfiguration config = super.getConfig();

        // If language key is not exist, create a new one
        if (config.getString("language") == null) {
            config.set("language", "en");
            config.save(super.getDataFolder() + "/config.yml");
        }

        // And return the language yaml configuration (or lazy-load the new one)
        return language == null ? (language = YamlConfiguration.loadConfiguration(new File(this.getLanguageFolder() + "/" + config.get("language") + ".yml"))) : language;
    }

}