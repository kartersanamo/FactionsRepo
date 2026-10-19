import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import commands.faction.admin.BypassSubCommand;
import commands.faction.admin.ChatSpySubCommand;
import commands.faction.admin.ConfigSubCommand;
import commands.faction.admin.DebugSubCommand;
import commands.faction.admin.GraceSubCommand;
import commands.faction.admin.PermanentSubCommand;
import commands.faction.admin.ReloadSubCommand;
import commands.faction.admin.SaveAllSubCommand;
import commands.faction.admin.StrikesSubCommand;
import commands.faction.admin.VersionSubCommand;
import commands.faction.claim.AutoClaimSubCommand;
import commands.faction.claim.ClaimSubCommand;
import commands.faction.claim.CornerSubCommand;
import commands.faction.claim.MapSubCommand;
import commands.faction.claim.SafeUnclaimAllSubCommand;
import commands.faction.claim.SeeChunkSubCommand;
import commands.faction.claim.SetBaseClaimSubCommand;
import commands.faction.claim.ShowClaimsSubCommand;
import commands.faction.claim.UnclaimAllSubCommand;
import commands.faction.claim.UnclaimSubCommand;
import commands.faction.claim.WarzoneUnclaimAllSubCommand;
import commands.faction.economy.BoostersSubCommand;
import commands.faction.economy.ChestSubCommand;
import commands.faction.economy.GiveBoosterSubCommand;
import commands.faction.economy.MoneySubCommand;
import commands.faction.economy.PointsSubCommand;
import commands.faction.economy.PowerSubCommand;
import commands.faction.economy.PowerboostSubCommand;
import commands.faction.economy.SetMaxVaultsSubCommand;
import commands.faction.economy.TNTFillSubCommand;
import commands.faction.economy.TNTSubCommand;
import commands.faction.economy.UpgradesSubCommand;
import commands.faction.economy.VaultSubCommand;
import commands.faction.economy.ViewChestSubCommand;
import commands.faction.info.AnnounceSubCommand;
import commands.faction.info.ChatSubCommand;
import commands.faction.info.DescriptionSubCommand;
import commands.faction.info.HelpSubCommand;
import commands.faction.info.ListSubCommand;
import commands.faction.info.LoginsSubCommand;
import commands.faction.info.LogsSubCommand;
import commands.faction.info.RulesSubCommand;
import commands.faction.info.ScoreboardSubCommand;
import commands.faction.info.ShowSubCommand;
import commands.faction.info.StatusSubCommand;
import commands.faction.info.TagSubCommand;
import commands.faction.info.TopSubCommand;
import commands.faction.membership.AltSubCommand;
import commands.faction.membership.CreateFactionSubCommand;
import commands.faction.membership.DeleteInviteSubCommand;
import commands.faction.membership.DisbandSubCommand;
import commands.faction.membership.InviteSubCommand;
import commands.faction.membership.JoinSubCommand;
import commands.faction.membership.LeaveSubCommand;
import commands.faction.membership.OpenSubCommand;
import commands.faction.membership.ReserveSubCommand;
import commands.faction.membership.RosterSubCommand;
import commands.faction.membership.ShowInvitesSubCommand;
import commands.faction.misc.BundleSubCommand;
import commands.faction.relation.AllySubCommand;
import commands.faction.relation.EnemySubCommand;
import commands.faction.relation.PeacefulSubCommand;
import commands.faction.relation.TruceSubCommand;
import commands.faction.role.*;
import commands.faction.teleport.CoordsSubCommand;
import commands.faction.teleport.DeleteHomeSubCommand;
import commands.faction.teleport.FlySubCommand;
import commands.faction.teleport.HomeSubCommand;
import commands.faction.teleport.NearSubCommand;
import commands.faction.teleport.SetHomeSubCommand;
import commands.faction.teleport.SetWarpSubCommand;
import commands.faction.teleport.StuckSubCommand;
import commands.faction.teleport.WarpSubCommand;
import commands.faction.teleport.WildSubCommand;
import listener.ClaimProtectionListener;
import listener.FactionListener;
import org.bukkit.plugin.java.JavaPlugin;
import systems.FactionSystem;

import java.util.Arrays;
import java.util.List;

public class FactionPlugin extends JavaPlugin {
    private FactionSystem factionSystem;

    @Override
    public void onEnable() {
        factionSystem = new FactionSystem(this);
        factionSystem.load();

        BukkitCommandManager manager = new BukkitCommandManager(this);
        for (BaseCommand command : createCommands(factionSystem)) {
            manager.registerCommand(command);
        }

        getServer().getPluginManager().registerEvents(new ClaimProtectionListener(factionSystem), this);
        getServer().getPluginManager().registerEvents(new FactionListener(factionSystem), this);
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
                new AltSubCommand(factionSystem),
                new JoinSubCommand(factionSystem),
                new OpenSubCommand(factionSystem),
                new RosterSubCommand(factionSystem),
                new ReserveSubCommand(factionSystem),
                new ShowInvitesSubCommand(factionSystem),
                new DeleteInviteSubCommand(factionSystem),
                new KickSubCommand(factionSystem),
                new BanSubCommand(factionSystem),
                new UnbanSubCommand(factionSystem),
                new PromoteSubCommand(factionSystem),
                new ColeaderSubCommand(factionSystem),
                new ModSubCommand(factionSystem),
                new OwnerSubCommand(factionSystem),
                new OwnerListSubCommand(factionSystem),
                new DefaultRankSubCommand(factionSystem),
                new TitleSubCommand(factionSystem),
                new PermissionsSubCommand(factionSystem),
                new AllySubCommand(factionSystem),
                new EnemySubCommand(factionSystem),
                new TruceSubCommand(factionSystem),
                new PeacefulSubCommand(factionSystem),
                new ClaimSubCommand(factionSystem),
                new UnclaimSubCommand(factionSystem),
                new UnclaimAllSubCommand(factionSystem),
                new AutoClaimSubCommand(factionSystem),
                new MapSubCommand(factionSystem),
                new ShowClaimsSubCommand(factionSystem),
                new SeeChunkSubCommand(factionSystem),
                new SetBaseClaimSubCommand(factionSystem),
                new CornerSubCommand(factionSystem),
                new SafeUnclaimAllSubCommand(factionSystem),
                new WarzoneUnclaimAllSubCommand(factionSystem),
                new SetHomeSubCommand(factionSystem),
                new HomeSubCommand(factionSystem),
                new DeleteHomeSubCommand(factionSystem),
                new SetWarpSubCommand(factionSystem),
                new WarpSubCommand(factionSystem),
                new StuckSubCommand(factionSystem),
                new WildSubCommand(factionSystem),
                new FlySubCommand(factionSystem),
                new NearSubCommand(factionSystem),
                new CoordsSubCommand(factionSystem),
                new MoneySubCommand(factionSystem),
                new PowerSubCommand(factionSystem),
                new PowerboostSubCommand(factionSystem),
                new PointsSubCommand(factionSystem),
                new TNTSubCommand(factionSystem),
                new TNTFillSubCommand(factionSystem),
                new VaultSubCommand(factionSystem),
                new ChestSubCommand(factionSystem),
                new ViewChestSubCommand(factionSystem),
                new BoostersSubCommand(factionSystem),
                new GiveBoosterSubCommand(factionSystem),
                new UpgradesSubCommand(factionSystem),
                new SetMaxVaultsSubCommand(factionSystem),
                new ShowSubCommand(factionSystem),
                new HelpSubCommand(factionSystem),
                new ListSubCommand(factionSystem),
                new TopSubCommand(factionSystem),
                new DescriptionSubCommand(factionSystem),
                new TagSubCommand(factionSystem),
                new RulesSubCommand(factionSystem),
                new AnnounceSubCommand(factionSystem),
                new ChatSubCommand(factionSystem),
                new ScoreboardSubCommand(factionSystem),
                new LoginsSubCommand(factionSystem),
                new LogsSubCommand(factionSystem),
                new StatusSubCommand(factionSystem),
                new VersionSubCommand(factionSystem),
                new SaveAllSubCommand(factionSystem),
                new ReloadSubCommand(factionSystem),
                new BypassSubCommand(factionSystem),
                new ChatSpySubCommand(factionSystem),
                new ConfigSubCommand(factionSystem),
                new DebugSubCommand(factionSystem),
                new GraceSubCommand(factionSystem),
                new PermanentSubCommand(factionSystem),
                new StrikesSubCommand(factionSystem),
                new BundleSubCommand(factionSystem)
        );
    }
}
