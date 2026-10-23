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

| Class | Purpose |
|---------|---------|
| `YamlFile` | Load/save any YAML in the plugin data folder, copy jar defaults, typed getters |
| `PluginConfig` | `config.yml` plus `playEffect(player, path)` for sound/particle groups |
| `PluginMessages` | `messages.yml` with color, placeholders, and `send` / `error` / `info` / `success` / `warning` |

Reload both with `pluginConfig.reload()` and `pluginMessages.reload()`. Player data files can use `new YamlFile(plugin, "homes/" + uuid + ".yml")` without a jar resource.

## Custom items

One class per item. The subclass owns id, slot, name, lore, and the rest of the appearance. Core tags the stack with
NBT and handles clicks. Child plugins register items and override `onUse` when they need behavior.

```java
items =new

CustomItemRegistry(this).

register(new ServerSelector());
        items.

giveAll(player);
```

```java
public final class ServerSelector extends CustomItem {
    public ServerSelector() {
        super("server-selector");
    }

    @Override
    public int slot() {
        return 4;
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
    protected void onUse(Player player) {
        new ServerSelectorMenu().open(player);
    }
}
```

Override `slot()` to place the item in a hotbar slot. Leave the default (`-1`) to add it to the first empty slot.

| Class                | Purpose                                                              |
|----------------------|----------------------------------------------------------------------|
| `NBTUtils`           | 1.8 NBT id tag/read/match (`ks_item`)                                |
| `CustomItem`         | One file per item: `create()`, `matches()`, `give()`, click handling |
| `CustomItemRegistry` | Register items, wire listeners, `giveAll(player)`                    |
| `ItemBuilder`        | Appearance builder, including `.glow()`                              |

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
