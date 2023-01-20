# Description
**Minority** is a functional library for fast and modern plugin development for Bukkit.  

At this moment, Minority have only one function: automatic generation of configs using annotations. You no longer need to write and update configs yourself every time, just mark the class that you want to configure in the future with the **@Section** annotation, and the fields that need to be added to the config with the **@Key** annotation, and after registering this class in the **ConfigurationWizard** configs will be ready. Also, Minority **automatically initializes** marked fields for registered classes using the Reflection API, which avoids confusion and errors due to inattention, because you only need to specify the field value once. Minority will do the rest for you.

## Example usage
```java
@Section(path = "monsters-skills",
        comment = "More dangerous monsters with their own skills will make gameplay more interesting.")
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

    public FuriousMonstersExampleFeature(JavaPlugin plugin) {
        super(plugin);
    }

    // This event will be automatically registered because of annotation detection in ConfigurationWizard.
    @EventHandler
    private void exampleOfAutoRegistration(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(message);
    }

}
```
### Registring the class
```java
class YourPlugin extends MinorityExtension {
    super.getConfigurationWizard().generate(FuriousMonstersExampleFeature.class);
}
```


### Result
```yaml

# This configuration file was automatically generated by Minority.

monsters-skills:
  # Wow! Say «No» to uncommented features in your configs!
  # And the second line is too!
  zombies-eat-brains: 'true'
  # This item will drop when this monster dies.
  item-drop: CAKE
  # Message? For real?
  join-message: Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur,
    adipisci velit...
```
