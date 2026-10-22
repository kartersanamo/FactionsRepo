# hub

Lobby plugin: join inventory and clickable hub items.

## Build

```bash
mvn -pl hub -am package
```

Install `core/target/Core.jar` and `hub/target/Hub.jar`. Hub depends on Core at runtime (`plugin.yml` `depend: [Core]`).

Main class: `com.kartersanamo.hub.HubPlugin`.

## Runtime

- Config: `plugins/Hub/config.yml`
- Messages: `plugins/Hub/messages.yml`

On join, Hub clears the inventory and gives every registered custom item. Items are identified with Core NBT ids, not
material type.

## Custom items

One class per item under `com.kartersanamo.hub.item`. Register it in `HubPlugin.onEnable`:

```java
items =new

CustomItemRegistry(this,pluginConfig, pluginMessages)
        .

register(new ServerSelector());
```

Appearance comes from config/messages. Override `onUse` in the item class for click behavior.

| Item            | Id                | Default slot      |
|-----------------|-------------------|-------------------|
| Server selector | `server-selector` | 4 (middle hotbar) |

The selector menu sends players to Bungee servers `dev-1` and `dev-2` with `BungeeUtil.connect`. Those names must match
the proxy config.
