package commands.faction.info;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.FactionChatMode;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ChatSubCommand extends FactionSubCommand {
    public ChatSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("c|chat")
    public void onChat(Player player, @Default("toggle") String mode) {
        if (factionSystem.getFaction(player.getUniqueId()) == null) {
            player.sendMessage("You are not in a faction.");
            return;
        }
        FactionChatMode next;
        if (mode.equalsIgnoreCase("public") || mode.equalsIgnoreCase("p")) next = FactionChatMode.PUBLIC;
        else if (mode.equalsIgnoreCase("faction") || mode.equalsIgnoreCase("f")) next = FactionChatMode.FACTION;
        else if (mode.equalsIgnoreCase("ally") || mode.equalsIgnoreCase("a")) next = FactionChatMode.ALLY;
        else {
            FactionChatMode current = factionSystem.getFlags().getChatMode(player.getUniqueId());
            next = current == FactionChatMode.PUBLIC ? FactionChatMode.FACTION
                    : current == FactionChatMode.FACTION ? FactionChatMode.ALLY : FactionChatMode.PUBLIC;
        }
        factionSystem.getFlags().setChatMode(player.getUniqueId(), next);
        player.sendMessage("Chat mode: " + next.name().toLowerCase());
    }
}
