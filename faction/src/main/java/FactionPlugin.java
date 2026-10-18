import co.aikar.commands.BukkitCommandManager;
import commands.faction.info.ShowSubCommand;
import commands.faction.membership.CreateFactionSubCommand;
import org.bukkit.plugin.java.JavaPlugin;
import systems.FactionSystem;

public class FactionPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        FactionSystem factionSystem = new FactionSystem();
        BukkitCommandManager manager = new BukkitCommandManager(this);
        manager.registerCommand(new CreateFactionSubCommand(factionSystem));
        manager.registerCommand(new ShowSubCommand(factionSystem));

        // TODO: Replace with Logger module call
        getLogger().info("Faction plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // TODO: Replace with Logger module call
        getLogger().info("Faction plugin has been disabled!");
    }
}
