# core

Shared Bukkit library used by other modules: menus, custom items, Bungee connect, teleport warmup, chat input, and YAML
config/messages.

## Build

```bash
mvn -pl core -am package
```

Install `core/target/Core.jar`. Other plugins should depend on this module with `provided` scope and `depend: [Core]` in `plugin.yml`.

Main class: `com.kartersanamo.core.CorePlugin`.

## Config and messages API

Each child plugin creates one `PluginConfig` and one `PluginMessages` in `onEnable`. Static accessors on the plugin class keep call sites short.

```java
pluginConfig = new PluginConfig(this);       // config.yml
pluginMessages = new PluginMessages(this);   // messages.yml

int max = HomePlugin.config().getInt("home.max-number", 10);
HomePlugin.messages().info(player, "home-set", "%name%", name);
HomePlugin.config().playEffect(player, "effects.set-home");
```

| Class            | Purpose                                                                                                 |
|------------------|---------------------------------------------------------------------------------------------------------|
| `YamlFile`       | Load/save any YAML in the plugin data folder, copy jar defaults, typed getters                          |
| `PluginConfig`   | `config.yml` plus `playEffect(player, path)` for sound/particle groups                                  |
| `PluginMessages` | `messages.yml` with color, placeholders, `send` / `sendList` / `error` / `info` / `success` / `warning` |

Reload both with `pluginConfig.reload()` and `pluginMessages.reload()`. Player data files can use `new YamlFile(plugin, "homes/" + uuid + ".yml")` without a jar resource.

Call-site placeholders are applied first, then `ServerBrandingUtil` values (`%p_color%`, `%s_color%`, `%server%`,
`%website%`, `%discord%`, and the rest). A call-site `%server%` therefore still wins for connect messages. `send` /
`sendList` make the phrases "terms of service" and "privacy policy" open the branding URLs.

## Custom items

One class per item for appearance, click behavior, and restrictions. Each plugin exposes those classes through an enum
that implements `CustomItems`. Core tags stacks with NBT, handles clicks, and enforces restriction flags. Slot is chosen
when you give the item, not on the class.

```java
public enum HubItems implements CustomItems {
    SERVER_SELECTOR(new ServerSelector());

    private final CustomItem item;

    HubItems(CustomItem item) {
        this.item = item;
    }

    @Override
    public CustomItem item() {
        return item;
    }
}
```

```java
CustomItems.register(this,HubItems .class);
HubItems.SERVER_SELECTOR.

give(player, 4);
```

Adding an item: create the class, add one enum constant, give it where you need it.

```java
public final class ServerSelector extends CustomItem {
    public ServerSelector() {
        super("server-selector");
    }

    @Override
    protected ItemStack appearance() {
        return new ItemBuilder(Material.COMPASS)
                .name("&a&lServer Selector")
                .lore("&7Click to choose a server")
                .glow()
                .build();
    }

    @Override
    public boolean canMoveFromSlot() {
        return false;
    }

    @Override
    protected void onUse(Player player) {
        new ServerSelectorMenu().open(player);
    }
}
```

Restriction flags default to `true`. Override a flag to `false` to cancel that action. Core listens for the matching
1.8 events (inventory click/drag including number-key swap, drop, death drops, pickup, consume, place, buckets, craft,
enchant, entity/item-frame/armor-stand/shear, projectiles/bows, and fishing).

| Flag                 | Default | Cancels                                               |
|----------------------|---------|-------------------------------------------------------|
| `canMoveFromSlot()`  | true    | Inventory click, drag, hotbar number-key swap         |
| `canDrop()`          | true    | Q drop and inventory drop clicks                      |
| `canDropOnDeath()`   | true    | Item appearing in death drops                         |
| `canPickup()`        | true    | Picking the item up off the ground                    |
| `canConsume()`       | true    | Eating / drinking                                     |
| `canPlace()`         | true    | Block place and emptying buckets                      |
| `canFill()`          | true    | Filling buckets                                       |
| `canCraft()`         | true    | Using the item in a crafting grid                     |
| `canEnchant()`       | true    | Enchanting table                                      |
| `canUseOnEntities()` | true    | Item frames, armor stands, shears, entity right-click |
| `canLaunch()`        | true    | Bows, pearls, snowballs, eggs, splash potions         |
| `canFish()`          | true    | Casting a fishing rod                                 |

`give(player)` adds to the first empty slot. `give(player, slot)` places it in that inventory slot.

| Class         | Purpose                                                       |
|---------------|---------------------------------------------------------------|
| `NBTUtils`    | 1.8 NBT id tag/read/match (`ks_item`)                         |
| `CustomItem`  | One file per item: appearance, `give`, restriction flags      |
| `CustomItems` | Plugin enum contract, `register`, and click handling via Core |
| `ItemBuilder` | Appearance builder, including `.glow()`                       |

Do not identify custom items by material or display name. `ItemBuilder` color-codes `&` in names and lore.

## Bungee connect

Core registers the `BungeeCord` outgoing channel on enable. Child plugins send players to a proxy server by Bungee name
(`dev-1`, `dev-2`, `hub`):

```java
BungeeUtil.connect(player, "dev-1");
```

This uses the plugin-messaging `Connect` subchannel. Do not dispatch `/server` or a proxy command from Bukkit.

| Class        | Purpose                                 |
|--------------|-----------------------------------------|
| `BungeeUtil` | Register the channel and send `Connect` |
