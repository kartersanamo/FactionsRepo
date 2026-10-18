package commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import messages.Messages;
import org.bukkit.command.CommandSender;
import systems.FactionSystem;

@CommandAlias("f|faction|factions")
public abstract class FactionSubCommand extends BaseCommand {

    protected final FactionSystem factionSystem;
    protected final Messages messages;

    protected FactionSubCommand(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
        this.messages = factionSystem.getMessages();
    }

    protected void msg(CommandSender sender, String path, Object... placeholders) {
        messages.send(sender, path, placeholders);
    }
}
