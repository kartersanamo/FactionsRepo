package commands.faction.admin;

import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ChatSpySubCommand extends FactionSubCommand {
    public ChatSpySubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("chatspy")
    @CommandPermission("factions.admin")
    public void onChatSpy(Player player) {
        boolean enabled = factionSystem.getFlags().toggleChatSpy(player.getUniqueId());
        msg(player, enabled ? "admin.chatspy-on" : "admin.chatspy-off");
    }
}
