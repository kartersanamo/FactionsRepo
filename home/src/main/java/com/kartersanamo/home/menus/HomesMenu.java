package com.kartersanamo.home.menus;

import com.kartersanamo.core.menu.Menu;
import com.kartersanamo.core.util.ColorUtil;
import com.kartersanamo.core.util.ItemBuilder;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HomesMenu extends Menu {

    private final HomeManager homeManager;
    private final Player player;

    public HomesMenu(HomeManager homeManager, Player player) {
        super(
                "homes_menu",
                HomePlugin.messages().get(
                        "homes-gui-title",
                        "%count%", String.valueOf(homeManager.getHomeCount(player.getUniqueId()))
                ),
                calculateSize(homeManager, player)
        );
        this.homeManager = homeManager;
        this.player = player;
        build();
    }

    private static int calculateSize(HomeManager homeManager, Player player) {
        int homeCount = homeManager.getHomeCount(player.getUniqueId());
        int minSize = HomePlugin.config().getGuiSize("gui.homes.min-size", 9);
        if (homeCount == 0) {
            return minSize;
        }
        int maxRows = HomePlugin.config().getInt("gui.homes.max-rows", 6, 1, 6);
        int rows = Math.min(maxRows, (int) Math.ceil(homeCount / 9.0));
        return Math.max(minSize, rows * 9);
    }

    public void build() {
        Map<String, Home> homes = homeManager.getHomes(player.getUniqueId());
        if (homes == null || homes.isEmpty()) {
            setItem(4, new ItemBuilder(Material.BARRIER)
                    .name(HomePlugin.messages().get("homes-gui-empty-name"))
                    .lore(HomePlugin.messages().get("homes-gui-empty-lore"))
                    .build());
            return;
        }

        int slot = 0;
        for (Home home : homes.values()) {
            if (slot >= getSize()) {
                break;
            }

            List<String> displayLore = new ArrayList<>();
            List<String> lore = home.getLore();
            if (lore != null && !lore.isEmpty()) {
                for (String line : lore) {
                    displayLore.add(ColorUtil.translate(line));
                }
                displayLore.add(HomePlugin.messages().get("blank-line"));
            }
            displayLore.add(HomePlugin.messages().get("homes-gui-instruction-teleport"));
            displayLore.add(HomePlugin.messages().get("homes-gui-instruction-manage"));

            ItemStack item = new ItemBuilder(home.getMaterial())
                    .name(HomePlugin.messages().get("homes-gui-home-name", "%name%", home.getDisplayName()))
                    .lore(displayLore)
                    .build();

            String homeName = home.getDisplayName();
            setItem(slot, item);
            setClickHandler(slot, event -> handleClick(event, homeName));
            slot++;
        }
    }

    private void handleClick(InventoryClickEvent event, String homeName) {
        if (!(event.getWhoClicked() instanceof Player clicker)) {
            return;
        }

        ClickType clickType = event.getClick();
        Home home = homeManager.getHome(player.getUniqueId(), homeName);
        if (home == null) {
            return;
        }

        if (clickType == ClickType.RIGHT || clickType == ClickType.SHIFT_RIGHT) {
            new ManageHomesMenu(homeManager, home, player).open(player);
        } else if (clickType == ClickType.LEFT || clickType == ClickType.SHIFT_LEFT) {
            clicker.closeInventory();
            home.teleport();
            homeManager.update(home);
        }
    }
}
