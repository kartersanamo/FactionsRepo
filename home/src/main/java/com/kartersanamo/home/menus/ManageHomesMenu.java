package com.kartersanamo.home.menus;

import com.kartersanamo.core.menu.Menu;
import com.kartersanamo.core.util.ItemBuilder;
import com.kartersanamo.core.util.LocationUtil;
import com.kartersanamo.home.HomePlugin;
import com.kartersanamo.home.managers.HomeManager;
import com.kartersanamo.home.models.Home;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManageHomesMenu extends Menu {

    private final HomeManager homeManager;
    private final Home home;
    private final Player player;

    public ManageHomesMenu(HomeManager homeManager, Home home, Player player) {
        super(
                "manage_home",
                HomePlugin.messages().get("manage-home-title"),
                HomePlugin.config().getGuiSize("gui.manage-home.size", 27)
        );
        this.homeManager = homeManager;
        this.home = home;
        this.player = player;
        build();
    }

    private void build() {
        if (home == null) {
            ItemStack nullItem = new ItemBuilder(Material.BARRIER)
                    .name(HomePlugin.messages().get("manage-home-error-name"))
                    .lore(List.of(
                            HomePlugin.messages().get("manage-home-error-line-1"),
                            HomePlugin.messages().get("manage-home-error-line-2"),
                            HomePlugin.messages().get("manage-home-error-line-3"),
                            HomePlugin.messages().get("manage-home-error-line-4")
                    ))
                    .build();
            setItem(13, nullItem);
            return;
        }

        setItem(11, new ItemBuilder(Material.NAME_TAG)
                .name(HomePlugin.messages().get("manage-home-change-name-title"))
                .lore(List.of(
                        HomePlugin.messages().get("manage-home-change-name-line-1"),
                        HomePlugin.messages().get("manage-home-change-name-line-2"),
                        HomePlugin.messages().get("blank-line"),
                        HomePlugin.messages().get("manage-home-current", "%value%", home.getDisplayName())
                ))
                .build());
        setClickHandler(11, event -> changeName());

        setItem(12, new ItemBuilder(home.getMaterial())
                .name(HomePlugin.messages().get("manage-home-change-material-title"))
                .lore(List.of(
                        HomePlugin.messages().get("manage-home-change-material-line-1"),
                        HomePlugin.messages().get("manage-home-change-material-line-2"),
                        HomePlugin.messages().get("blank-line"),
                        HomePlugin.messages().get("manage-home-current", "%value%", home.getMaterial().toString())
                ))
                .build());
        setClickHandler(12, event -> changeMaterial());

        List<String> lore = new ArrayList<>();
        lore.add(HomePlugin.messages().get("manage-home-change-lore-line-1"));
        lore.add(HomePlugin.messages().get("manage-home-change-lore-line-2"));
        lore.add(HomePlugin.messages().get("blank-line"));
        lore.add(HomePlugin.messages().get("manage-home-current-label"));
        List<String> currentLore = home.getLore();
        if (currentLore == null || currentLore.isEmpty()) {
            lore.add(HomePlugin.messages().get("manage-home-lore-none"));
        } else {
            for (String line : currentLore) {
                lore.add(HomePlugin.messages().get("manage-home-lore-entry", "%line%", line));
            }
        }
        setItem(13, new ItemBuilder(Material.PAPER)
                .name(HomePlugin.messages().get("manage-home-change-lore-title"))
                .lore(lore)
                .build());
        setClickHandler(13, event -> changeLore());

        setItem(14, new ItemBuilder(Material.MAP)
                .name(HomePlugin.messages().get("manage-home-change-location-title"))
                .lore(List.of(
                        HomePlugin.messages().get("manage-home-change-location-line-1"),
                        HomePlugin.messages().get("manage-home-change-location-line-2"),
                        HomePlugin.messages().get("blank-line"),
                        HomePlugin.messages().get("manage-home-current", "%value%", LocationUtil.format(home.getLocation()))
                ))
                .build());
        setClickHandler(14, event -> changeLocation());

        setItem(15, new ItemBuilder(Material.BARRIER)
                .name(HomePlugin.messages().get("manage-home-delete-title"))
                .lore(List.of(
                        HomePlugin.messages().get("manage-home-delete-line-1"),
                        HomePlugin.messages().get("manage-home-delete-line-2"),
                        HomePlugin.messages().get("manage-home-delete-line-3"),
                        HomePlugin.messages().get("manage-home-delete-line-4")
                ))
                .build());
        setClickHandler(15, event -> deleteHome());

        setItem(18, new ItemBuilder(Material.ARROW)
                .name(HomePlugin.messages().get("manage-home-back-title"))
                .lore(List.of(
                        HomePlugin.messages().get("manage-home-back-line-1"),
                        HomePlugin.messages().get("manage-home-back-line-2")
                ))
                .build());
        setClickHandler(18, event -> backButton());

        setItem(22, new ItemBuilder(Material.BOOK)
                .name(HomePlugin.messages().get("manage-home-info-title"))
                .lore(homeManager.getInformationLines(home))
                .build());
        setClickHandler(22, event -> homeManager.sendInfo(home, player));
    }

    private void changeName() {
        player.closeInventory();
        HomePlugin.getInstance().getChatInputManager().awaitInput(
                player,
                HomePlugin.messages().get("prompt-change-name"),
                input -> {
                    String oldName = home.getDisplayName();
                    if (homeManager.isHomeNameCorrectSize(input)) {
                        HomePlugin.messages().error(player, "home-name-invalid");
                    } else {
                        home.setDisplayName(input);
                        homeManager.update(home);
                        HomePlugin.messages().info(player, "home-name-changed", "%old%", oldName, "%new%", input);
                        HomePlugin.config().playEffect(player, "effects.rename-home");
                    }
                    new ManageHomesMenu(homeManager, home, player).open(player);
                },
                () -> HomePlugin.messages().error(player, "name-input-cancelled")
        );
    }

    private void changeMaterial() {
        player.closeInventory();
        HomePlugin.getInstance().getChatInputManager().awaitInput(
                player,
                HomePlugin.messages().get("prompt-change-material"),
                input -> {
                    Material newMaterial = Material.matchMaterial(input);
                    Material oldMaterial = home.getMaterial();
                    if (newMaterial == null) {
                        HomePlugin.messages().error(player, "home-material-invalid");
                    } else {
                        home.setMaterial(newMaterial);
                        homeManager.update(home);
                        HomePlugin.messages().info(
                                player,
                                "home-material-updated",
                                "%old%", oldMaterial.name(),
                                "%new%", newMaterial.name()
                        );
                        HomePlugin.config().playEffect(player, "effects.edit-material");
                    }
                    new ManageHomesMenu(homeManager, home, player).open(player);
                },
                () -> HomePlugin.messages().error(player, "material-input-cancelled")
        );
    }

    private void changeLore() {
        player.closeInventory();
        HomePlugin.getInstance().getChatInputManager().awaitInput(
                player,
                HomePlugin.messages().get("prompt-change-lore"),
                input -> {
                    if (input == null || input.trim().isEmpty()) {
                        HomePlugin.messages().error(player, "home-lore-empty");
                        return;
                    }
                    List<String> lore = Arrays.stream(input.split("\\|"))
                            .map(String::trim)
                            .filter(line -> !line.isEmpty())
                            .toList();
                    if (lore.isEmpty()) {
                        HomePlugin.messages().error(player, "home-lore-min-lines");
                        return;
                    }
                    home.setLore(lore);
                    homeManager.update(home);
                    HomePlugin.messages().info(player, "home-lore-updated", "%count%", String.valueOf(lore.size()));
                    HomePlugin.config().playEffect(player, "effects.edit-lore");
                    new ManageHomesMenu(homeManager, home, player).open(player);
                },
                () -> HomePlugin.messages().error(player, "lore-input-cancelled")
        );
    }

    private void changeLocation() {
        Location oldLocation = home.getLocation();
        home.setLocation(player.getLocation());
        homeManager.update(home);
        new ManageHomesMenu(homeManager, home, player).open(player);
        HomePlugin.messages().info(
                player,
                "home-location-updated",
                "%old%", LocationUtil.format(oldLocation),
                "%new%", LocationUtil.format(player.getLocation())
        );
        HomePlugin.config().playEffect(player, "effects.edit-location");
    }

    private void deleteHome() {
        player.closeInventory();
        String confirm = HomePlugin.messages().getOr("prompt-delete-confirm-value", "confirm");
        HomePlugin.getInstance().getChatInputManager().awaitInput(
                player,
                HomePlugin.messages().get("prompt-delete-home", "%confirm%", confirm),
                input -> {
                    if (input.equalsIgnoreCase(confirm)) {
                        String name = home.getDisplayName();
                        homeManager.deleteHome(player.getUniqueId(), name);
                        HomePlugin.messages().info(player, "home-deleted", "%name%", name);
                        HomePlugin.config().playEffect(player, "effects.delete-home");
                        new HomesMenu(homeManager, player).open(player);
                    } else {
                        HomePlugin.messages().error(player, "home-deletion-cancelled");
                        new ManageHomesMenu(homeManager, home, player).open(player);
                    }
                },
                () -> HomePlugin.messages().error(player, "home-deletion-cancelled")
        );
    }

    private void backButton() {
        player.closeInventory();
        new HomesMenu(homeManager, player).open(player);
    }
}
