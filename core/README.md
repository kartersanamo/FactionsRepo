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

One class per item. Core loads appearance from config/messages, tags the stack with NBT, and handles clicks. Child
plugins register items and override `onUse` when they need behavior.

```java
items =new

CustomItemRegistry(this,pluginConfig, pluginMessages)
        .

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
    protected void onUse(Player player) {
        new ServerSelectorMenu(player).open(player);
    }
}
```

`config.yml` owns material, slot, amount, and glow. `messages.yml` owns name and lore. Paths default to `items.<id>`.

```yaml
items:
  server-selector:
    slot: 4
    material: COMPASS
    amount: 1
    glow: true
```

```yaml
items:
  server-selector:
    name: "&a&lServer Selector"
    lore:
      - "&7Click to choose a server"
```

| Class                | Purpose                                                              |
|----------------------|----------------------------------------------------------------------|
| `ItemIds`            | 1.8 NBT id tag/read/match (`ks_item`)                                |
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
