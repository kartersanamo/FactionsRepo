import co.aikar.commands.BukkitCommandManager;
import commands.faction.membership.CreateFactionSubCommand;
import org.bukkit.plugin.java.JavaPlugin;

public class FactionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new CreateFactionSubCommand());

        // TODO: Replace with Logger module call
        getLogger().info("Faction plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // TODO: Replace with Logger module call
        getLogger().info("Faction plugin has been disabled!");
    }
}
