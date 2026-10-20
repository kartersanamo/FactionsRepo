# core

Shared Bukkit library used by other modules: menus, teleport warmup, chat input, and YAML config/messages.

## Build

```bash
mvn -pl core -am package
```

Install `core/target/core-1.0.0.jar`. Other plugins should depend on this module with `provided` scope and `depend: [Core]` in `plugin.yml`.

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
