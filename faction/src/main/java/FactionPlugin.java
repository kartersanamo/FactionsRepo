import org.bukkit.plugin.java.JavaPlugin;

public class FactionPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("Faction plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("Faction plugin has been disabled!");
    }
}
