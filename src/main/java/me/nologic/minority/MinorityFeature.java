package me.nologic.minority;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class MinorityFeature {

    protected JavaPlugin plugin;

    public MinorityFeature(JavaPlugin plugin) {
        this.plugin = plugin;
    }

}