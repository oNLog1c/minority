package me.nologic.minority.plugin;

import io.papermc.paper.event.player.AsyncChatEvent;
import lombok.Setter;
import me.nologic.minority.MinorityExtension;
import me.nologic.minority.MinorityFeature;
import me.nologic.minority.annotations.Key;
import me.nologic.minority.annotations.Translatable;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@Translatable
public class FeatureShowcase extends MinorityExtension implements Listener, MinorityFeature {

    /* Shouldn't be final or static, or magic will not work. */
    @Key(section = "message", path = "answer-for-meaning-of-life", value = "42", comment = { "Comment!? Why?" })
    private String answer;

    @Override
    public void onEnable() {
        super.getConfigurationWizard().generate(this.getClass());
        init(this, this.getClass(), this);
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerChat(AsyncChatEvent event) {
        event.getPlayer().sendMessage("Answer: " + answer);
    }

}