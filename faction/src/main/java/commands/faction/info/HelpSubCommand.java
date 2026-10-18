package commands.faction.info;

import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class HelpSubCommand extends FactionSubCommand {
    public HelpSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @HelpCommand
    @Subcommand("help")
    public void onHelp(Player player) {
        player.sendMessage("Faction commands:");
        player.sendMessage("/f create, join, leave, disband, invite, kick, ban");
        player.sendMessage("/f claim, unclaim, unclaimall, map, autoclaim, home, warp");
        player.sendMessage("/f show, list, top, roster, ally, enemy, money, tnt, power");
        player.sendMessage("/f chat, announce, description, tag, rules, fly, near, stuck, wild");
    }
}
