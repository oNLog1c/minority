# Description
**Minority** is a functional library for fast and modern Bukkit plugin development, which provides simple but powerful API for **automatically generating configurations and language files**. Everything works with the help of **annotations** and **reflection**. Working with configs (especially custom ones), as well as supporting different languages ​​for a programmer, has always been a living hell. This will be especially understood by those who have developed more or less complex plugins more than once. With **Minority**, this becomes a much easier task, which, suddenly, can even be enjoyable in its simplicity.

## TODO
- ✓ Language annotations ✓
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
    <artifactId>minority</artifactId>
    <version>1.3</version>
</dependency>
```

## Automatic config generation
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


## Automatic language.yml generation

Adding a language support to your plugin never be that easy.
Add **@Translatable** annotation to your class, then just use **@Key** to describe the translatable fields. Your class **must** implement the **MinorityFeature** interface.
```java
@Translatable
public class MessageSender implements MinorityFeature, Listener {
    
    @Key(section = "messages", path = "join-message", value = "Hello, %s! You can see this message only when PlayerJoinEvent fires!")
    private /* Fields NEVER shouldn't be final if you want to init it automatically! */ String joinMessage; 
    
    // Message initialization (can be manual or automatic)
    public MessageSender(final MinorityExtension plugin) {
    	plugin.getConfigurationWizard().generate(this.getClass());
	
        // Manual field initialization
        // this.joinMessage = plugin.getLanguage().getString("messages.join-message");
	
        // Or automatic (it will init all fields with @Key annotation using reflection)
        // If you prefer this method, you shouldn't make fields with @Key annotation final.
        this.init(this, this.getClass(), plugin);
    }
    
    @EventHandler
    private void onPlayerJoin(PlayerJoinEvent event) {
        event.getPlayer().sendMessage(joinMessage.formatted(event.getPlayer().getName()));
    }
    
}
```
*Voila*, you now have a class that, when created, will generate keys in a language yaml file in the plugin language directory and automatically initialize all language fields. By default, the language file will be named **en.yml**.

### Result (en.yml)
```yaml

# This language file was automatically generated with Minority.

messages:
  join-message: Hello, %s! You can see this message only when PlayerJoinEvent fires!
```
![minority](https://github.com/oNLog1c/Minority/assets/53514252/10144687-2b12-4f04-85c9-9b610e1d636e)
