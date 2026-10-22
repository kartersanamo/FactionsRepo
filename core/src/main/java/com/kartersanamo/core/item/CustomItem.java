package com.kartersanamo.core.item;

import com.kartersanamo.core.config.PluginConfig;
import com.kartersanamo.core.config.PluginMessages;
import com.kartersanamo.core.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public abstract class CustomItem implements Listener {

    private final String id;
    private CustomItemRegistry registry;

    protected CustomItem(String id) {
        this.id = id;
    }

    public final String id() {
        return id;
    }

    void bind(CustomItemRegistry registry) {
        this.registry = registry;
    }

    public int slot() {
        return config().getInt(configPath() + ".slot", -1);
    }

    public ItemStack create() {
        return NBTUtils.tag(appearance(), id);
    }

    public boolean matches(ItemStack item) {
        return NBTUtils.is(item, id);
    }

    public void give(Player player) {
        if (player == null) {
            return;
        }
        ItemStack item = create();
        PlayerInventory inventory = player.getInventory();
        int slot = slot();
        if (slot >= 0 && slot < inventory.getSize()) {
            inventory.setItem(slot, item);
            return;
        }
        inventory.addItem(item);
    }

    protected ItemStack appearance() {
        String path = configPath();
        ItemBuilder builder = new ItemBuilder(config().getMaterial(path + ".material", Material.STONE))
                .amount(config().getInt(path + ".amount", 1))
                .name(messages().get(path + ".name"))
                .lore(messages().getList(path + ".lore"));
        if (config().getBoolean(path + ".glow", false)) {
            builder.glow();
        }
        return builder.build();
    }

    protected void onUse(Player player) {
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.PHYSICAL) {
            return;
        }
        if (!matches(event.getItem())) {
            return;
        }
        event.setCancelled(true);
        onUse(event.getPlayer());
    }

    protected String configPath() {
        return registry().configRoot() + "." + id;
    }

    protected PluginConfig config() {
        return registry().config();
    }

    protected PluginMessages messages() {
        return registry().messages();
    }

    private CustomItemRegistry registry() {
        if (registry == null) {
            throw new IllegalStateException("Custom item '" + id + "' must be registered before use");
        }
        return registry;
    }
}
