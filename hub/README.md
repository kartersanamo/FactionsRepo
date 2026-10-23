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

On join, Hub sends `welcome-messages` with `%player%` and branding placeholders, clears the inventory, and gives
`HubItems.SERVER_SELECTOR` in slot 4. "terms of service" and "privacy policy" in that message open the URLs from
`ServerBrandingUtil`. Items are identified with Core NBT ids, not material type.

## Custom items

One class per item under `com.kartersanamo.hub.item`, listed on the `HubItems` enum. Register the enum in
`HubPlugin.onEnable`:

```java
CustomItems.register(this, HubItems.class);
```

Name, lore, and material live on the item class. Pass the slot when giving:

```java
HubItems.SERVER_SELECTOR.give(player, 4);
```

| Item            | Enum                       | Id                |
|-----------------|----------------------------|-------------------|
| Server selector | `HubItems.SERVER_SELECTOR` | `server-selector` |

The selector menu sends players to Bungee servers `dev-1` and `dev-2` with `BungeeUtil.connect`. Those names must match
the proxy config.
