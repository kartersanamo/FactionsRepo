package commands.faction.info;

import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import co.aikar.commands.annotation.Subcommand;
import commands.FactionSubCommand;
import org.bukkit.entity.Player;
import systems.FactionSystem;

public class HelpSubCommand extends FactionSubCommand {

    public HelpSubCommand(FactionSystem factionSystem) {
        super(factionSystem);
    }

    @HelpCommand
    @Subcommand("help")
    public void onHelp(Player player, @Optional Integer page) {
        int playerPages = countPages("help-");
        int adminPages = player.isOp() ? countPages("help-admin-") : 0;
        int total = playerPages + adminPages;
        if (total < 1) {
            return;
        }

        int selected = page == null ? 1 : page;
        if (selected < 1 || selected > total) {
            selected = 1;
        }

        String path;
        if (selected <= playerPages) {
            path = "help-" + selected;
        } else {
            path = "help-admin-" + (selected - playerPages);
        }

        int next = selected >= total ? 1 : selected + 1;
        msg(player, path, "page", selected, "total", total);
        msg(player, "help-footer", "page", selected, "total", total, "next", next);
        msg(player, "help-end");
    }

    private int countPages(String prefix) {
        int count = 0;
        while (messages.hasList(prefix + (count + 1))) {
            count++;
        }
        return count;
    }
}
