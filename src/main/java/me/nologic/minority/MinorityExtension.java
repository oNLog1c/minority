package me.nologic.minority;

import me.nologic.minority.config.ConfigurationWizard;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinorityExtension extends JavaPlugin {

    // TODO: add comments
    private ConfigurationWizard configurationWizard;

    public ConfigurationWizard getConfigurationWizard() {
        return configurationWizard == null ? (configurationWizard = new ConfigurationWizard(this)) : this.configurationWizard;
    }

}