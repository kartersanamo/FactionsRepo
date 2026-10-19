package commands.faction.info;

import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import model.Faction;
import model.FactionChatMode;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class ChatSubCommand extends FactionSubCommand {
    public ChatSubCommand(FactionSystem factionSystem) { super(factionSystem); }

    @Subcommand("c|chat")
    public void onChat(Player player, @Default("toggle") String mode) {
        if (factionSystem.getFaction(player.getUniqueId()) == null) {
            msg(player, "general.not-in-faction");
            return;
        }
        FactionChatMode next;
        if (mode.equalsIgnoreCase("public") || mode.equalsIgnoreCase("p")) next = FactionChatMode.PUBLIC;
        else if (mode.equalsIgnoreCase("faction") || mode.equalsIgnoreCase("f")) next = FactionChatMode.FACTION;
        else if (mode.equalsIgnoreCase("ally") || mode.equalsIgnoreCase("a")) next = FactionChatMode.ALLY;
        else if (mode.equalsIgnoreCase("truce") || mode.equalsIgnoreCase("t")) next = FactionChatMode.TRUCE;
        else {
            FactionChatMode current = factionSystem.getFlags().getChatMode(player.getUniqueId());
            next = current == FactionChatMode.PUBLIC ? FactionChatMode.FACTION
                    : current == FactionChatMode.FACTION ? FactionChatMode.ALLY
                    : current ==  FactionChatMode.ALLY ? FactionChatMode.TRUCE : FactionChatMode.PUBLIC;
        }
        factionSystem.getFlags().setChatMode(player.getUniqueId(), next);
        msg(player, "chat.mode", "mode", next.name().toLowerCase());
    }
}
