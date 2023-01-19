package me.nologic.minority.feature;

import me.nologic.minority.MinorityFeature;
import me.nologic.minority.annotations.Section;
import me.nologic.minority.annotations.Key;
import me.nologic.minority.annotations.Type;
import org.bukkit.Material;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Section(path = "monsters-skills", comment = "More dangerous monsters with their own skills will make gameplay more interesting.")
public class FuriousMonstersExampleFeature extends MinorityFeature implements Listener {

    @Key(path = "zombies-eat-brains", type = Type.BOOLEAN, value = "true",
    comment = { "Wow! Say «No» to uncommented features in your configs!",
            "And the second line is too!" } )
    public boolean canJump;

    @Key(path = "item-drop", type = Type.ENUM, value = "CAKE",
    comment = { "This item will drop when this monster dies." })
    public Material drop;

    @Key(path = "join-message", type = Type.STRING, value = "Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit...",
    comment = "Message? For real?")
    public String message;

    // This event will be automatically registered because of annotation detection in ConfigurationWizard.
    @EventHandler
    private void exampleOfAutoRegistration(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(message);
    }

}