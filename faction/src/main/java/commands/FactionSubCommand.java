package commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import lombok.RequiredArgsConstructor;
import systems.FactionSystem;

@RequiredArgsConstructor
@CommandAlias("f|faction|factions")
public abstract class FactionSubCommand extends BaseCommand {

    public FactionSubCommand() {
        throw new UnsupportedOperationException("FactionSubCommand must be constructed with a FactionSystem instance.");
    }

    protected final FactionSystem factionSystem;
}
