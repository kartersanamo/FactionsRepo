package commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import systems.FactionSystem;

@CommandAlias("f|faction|factions")
public abstract class FactionSubCommand extends BaseCommand {

    protected final FactionSystem factionSystem;

    protected FactionSubCommand(FactionSystem factionSystem) {
        this.factionSystem = factionSystem;
    }
}
