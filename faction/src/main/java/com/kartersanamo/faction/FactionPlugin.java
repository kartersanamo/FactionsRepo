package com.kartersanamo.faction;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import com.kartersanamo.faction.commands.faction.admin.BypassSubCommand;
import com.kartersanamo.faction.commands.faction.admin.ChatSpySubCommand;
import com.kartersanamo.faction.commands.faction.admin.ConfigSubCommand;
import com.kartersanamo.faction.commands.faction.admin.DebugSubCommand;
import com.kartersanamo.faction.commands.faction.admin.GraceSubCommand;
import com.kartersanamo.faction.commands.faction.admin.PermanentSubCommand;
import com.kartersanamo.faction.commands.faction.admin.ReloadSubCommand;
import com.kartersanamo.faction.commands.faction.admin.SaveAllSubCommand;
import com.kartersanamo.faction.commands.faction.admin.StrikesSubCommand;
import com.kartersanamo.faction.commands.faction.admin.VersionSubCommand;
import com.kartersanamo.faction.commands.faction.claim.AutoClaimSubCommand;
import com.kartersanamo.faction.commands.faction.claim.ClaimSubCommand;
import com.kartersanamo.faction.commands.faction.claim.CornerSubCommand;
import com.kartersanamo.faction.commands.faction.claim.MapSubCommand;
import com.kartersanamo.faction.commands.faction.claim.SafeUnclaimAllSubCommand;
import com.kartersanamo.faction.commands.faction.claim.SeeChunkSubCommand;
import com.kartersanamo.faction.commands.faction.claim.SetBaseClaimSubCommand;
import com.kartersanamo.faction.commands.faction.claim.ShowClaimsSubCommand;
import com.kartersanamo.faction.commands.faction.claim.UnclaimAllSubCommand;
import com.kartersanamo.faction.commands.faction.claim.UnclaimSubCommand;
import com.kartersanamo.faction.commands.faction.claim.WarzoneUnclaimAllSubCommand;
import com.kartersanamo.faction.commands.faction.economy.BoostersSubCommand;
import com.kartersanamo.faction.commands.faction.economy.ChestSubCommand;
import com.kartersanamo.faction.commands.faction.economy.GiveBoosterSubCommand;
import com.kartersanamo.faction.commands.faction.economy.MoneySubCommand;
import com.kartersanamo.faction.commands.faction.economy.PointsSubCommand;
import com.kartersanamo.faction.commands.faction.economy.PowerSubCommand;
import com.kartersanamo.faction.commands.faction.economy.PowerboostSubCommand;
import com.kartersanamo.faction.commands.faction.economy.SetMaxVaultsSubCommand;
import com.kartersanamo.faction.commands.faction.economy.TNTFillSubCommand;
import com.kartersanamo.faction.commands.faction.economy.TNTSubCommand;
import com.kartersanamo.faction.commands.faction.economy.UpgradesSubCommand;
import com.kartersanamo.faction.commands.faction.economy.VaultSubCommand;
import com.kartersanamo.faction.commands.faction.economy.ViewChestSubCommand;
import com.kartersanamo.faction.commands.faction.info.AnnounceSubCommand;
import com.kartersanamo.faction.commands.faction.info.ChatSubCommand;
import com.kartersanamo.faction.commands.faction.info.DescriptionSubCommand;
import com.kartersanamo.faction.commands.faction.info.HelpSubCommand;
import com.kartersanamo.faction.commands.faction.info.ListSubCommand;
import com.kartersanamo.faction.commands.faction.info.LoginsSubCommand;
import com.kartersanamo.faction.commands.faction.info.LogsSubCommand;
import com.kartersanamo.faction.commands.faction.info.RulesSubCommand;
import com.kartersanamo.faction.commands.faction.info.ScoreboardSubCommand;
import com.kartersanamo.faction.commands.faction.info.ShowSubCommand;
import com.kartersanamo.faction.commands.faction.info.StatusSubCommand;
import com.kartersanamo.faction.commands.faction.info.TagSubCommand;
import com.kartersanamo.faction.commands.faction.info.TopSubCommand;
import com.kartersanamo.faction.commands.faction.membership.AltSubCommand;
import com.kartersanamo.faction.commands.faction.membership.CreateFactionSubCommand;
import com.kartersanamo.faction.commands.faction.membership.DeleteInviteSubCommand;
import com.kartersanamo.faction.commands.faction.membership.DisbandSubCommand;
import com.kartersanamo.faction.commands.faction.membership.InviteSubCommand;
import com.kartersanamo.faction.commands.faction.membership.JoinSubCommand;
import com.kartersanamo.faction.commands.faction.membership.LeaveSubCommand;
import com.kartersanamo.faction.commands.faction.membership.OpenSubCommand;
import com.kartersanamo.faction.commands.faction.membership.ReserveSubCommand;
import com.kartersanamo.faction.commands.faction.membership.RosterSubCommand;
import com.kartersanamo.faction.commands.faction.membership.ShowInvitesSubCommand;
import com.kartersanamo.faction.commands.faction.misc.BundleSubCommand;
import com.kartersanamo.faction.commands.faction.relation.AllySubCommand;
import com.kartersanamo.faction.commands.faction.relation.EnemySubCommand;
import com.kartersanamo.faction.commands.faction.relation.PeacefulSubCommand;
import com.kartersanamo.faction.commands.faction.relation.TruceSubCommand;
import com.kartersanamo.faction.commands.faction.role.*;
import com.kartersanamo.faction.commands.faction.teleport.CoordsSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.DeleteHomeSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.FlySubCommand;
import com.kartersanamo.faction.commands.faction.teleport.HomeSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.NearSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.SetHomeSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.SetWarpSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.StuckSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.WarpSubCommand;
import com.kartersanamo.faction.commands.faction.teleport.WildSubCommand;
import com.kartersanamo.faction.listener.ClaimProtectionListener;
import com.kartersanamo.faction.listener.FactionListener;
import org.bukkit.plugin.java.JavaPlugin;
import com.kartersanamo.faction.systems.FactionSystem;

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
