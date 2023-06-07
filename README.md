# Description
**Minority** is a functional library for fast and modern Bukkit plugin development.  

At this moment, Minority have only one function: automatic generation of configs using annotations. You no longer need to write and update configs yourself every time, just mark the class that you want to configure in the future with the **@Section** annotation, and the fields that need to be added to the config with the **@Key** annotation. After registering this class in **ConfigurationWizard** and enabling the server, config will be ready!  

Also, Minority **automatically initializes** marked fields in registered classes using the Reflection API, which avoids confusion and errors due to inattention, because you only need to specify the field value once. Minority will do the rest for you.

## TODO
- Language annotations
- Easy SQLite DB creation
- Custom biome API
- QoL-functions in MinorityExpansion

## Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>

<dependency>
    <groupId>com.github.oNLog1c</groupId>
    <artifactId>Minority</artifactId>
    <version>1.0</version>
</dependency>
```

## Config generation
```java
@Section(path = "monsters-skills",
        comment = "More dangerous monsters with their own skills will make gameplay more interesting.")
public class FuriousMonstersExampleFeature implements Listener, MinorityFeature {

    @Key(path = "zombies-eat-brains", type = Type.BOOLEAN, value = "true",
    comment = { "Wow! Say «No» to uncommented configs!",
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
```
### Registering the class
```java
class YourPlugin extends MinorityExtension {
    // MinorityExtension extends from JavaPlugin, so it can be used as the Main class in plugin.yml
    super.getConfigurationWizard().generate(FuriousMonstersExampleFeature.class);
}
```


### Result
```yaml

# This configuration file was automatically generated by Minority.

monsters-skills:
  # Wow! Say «No» to uncommented configs!
  # And the second line is too!
  zombies-eat-brains: 'true'
  # This item will drop when this monster dies.
  item-drop: CAKE
  # Message? For real?
  join-message: Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur,
    adipisci velit...
```


## Easy translation

Adding a language support to your plugin never be that easy.
Add **@Translatable** annotation to your class, then just use **@Key** to describe the translatable fields. Your class **must** implement the **MinorityFeature** interface.
```java
@Translatable
public class MessageSender implements MinorityFeature, Listener {
    
    @Key(section = "messages", path = "join-message", value = "Hello, %s! You can see this message only when PlayerJoinEvent fires!")
    private /* Fields NEVER shouldn't be final if you want to init it automatically! */ String joinMessage; 
    
    // Message initialization (can be manual or automatic)
    public MessageSender(final MinorityExtension plugin) {
    	plugin.getConfigurationWizard().generate(this);
	
	// Automatic initialization (it will init all fields with @Key annotation using reflection)
	this.init(this, this.getClass(), plugin);
	
	// Or the usual manual field initialization, if you don't want to do it automatically for some reason.
	this.joinMessage = plugin.getLanguage().getString("messages.join-message");
    }
    
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(joinMessage.formatted(event.getPlayer().getName()));
    }
    
}
```
Voila, you now have a class that, when created, will generate keys in a language yaml file in the plugin directory (./languages) and automatically initialize all language fields. 
By default, the language file will be named en.yml.

```java
// This usually happens in your plugin's main class, but you can do it elsewhere.
@Override  
public void onEnable() {
	final MessageSender sender = new MessageSender();
	Bukkit.getServer().getPluginManager().registerEvents(sender, this);  
}
```
