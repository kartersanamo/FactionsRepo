import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import commands.faction.claim.ClaimSubCommand;
import commands.faction.claim.MapSubCommand;
import commands.faction.claim.UnclaimSubCommand;
import commands.faction.info.ShowSubCommand;
import commands.faction.membership.CreateFactionSubCommand;
import commands.faction.membership.DisbandSubCommand;
import commands.faction.membership.InviteSubCommand;
import commands.faction.membership.JoinSubCommand;
import commands.faction.membership.LeaveSubCommand;
import commands.faction.role.KickSubCommand;
import listener.ClaimProtectionListener;
import org.bukkit.plugin.java.JavaPlugin;
import systems.FactionSystem;

import java.util.Arrays;
import java.util.List;

public class FactionPlugin extends JavaPlugin {
    private FactionSystem factionSystem;

    @Override
    public void onEnable() {
        factionSystem = new FactionSystem(getDataFolder(), getLogger());
        factionSystem.load();

        BukkitCommandManager manager = new BukkitCommandManager(this);
        for (BaseCommand command : createCommands(factionSystem)) {
            manager.registerCommand(command);
        }

        getServer().getPluginManager().registerEvents(new ClaimProtectionListener(factionSystem), this);
        getLogger().info("Faction plugin has been enabled!");
    }

    @Override
    public void onDisable() {
        if (factionSystem != null) {
            factionSystem.save();
        }
        getLogger().info("Faction plugin has been disabled!");
    }

    private static List<BaseCommand> createCommands(FactionSystem factionSystem) {
        return Arrays.asList(
                new CreateFactionSubCommand(factionSystem),
                new LeaveSubCommand(factionSystem),
                new DisbandSubCommand(factionSystem),
                new InviteSubCommand(factionSystem),
                new JoinSubCommand(factionSystem),
                new KickSubCommand(factionSystem),
                new ShowSubCommand(factionSystem),
                new ClaimSubCommand(factionSystem),
                new UnclaimSubCommand(factionSystem),
                new MapSubCommand(factionSystem)
        );
    }
}
