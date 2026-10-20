# home

Player homes plugin: set/teleport/delete homes, GUIs, and admin management.

## Build

```bash
mvn -pl home -am package
```

Install `core/target/core-1.0.0.jar` and `home/target/home-1.0.0.jar`. Home depends on Core at runtime (`plugin.yml` `depend: [Core]`).

Main class: `com.kartersanamo.home.HomePlugin`.

## Runtime

- Config: `plugins/Home/config.yml`
- Messages: `plugins/Home/messages.yml`
- Homes: `plugins/Home/homes/<uuid>.yml`
- `/homeplugin reload` reloads config and messages (`home.reload`)

Call sites use the core YAML API: `HomePlugin.config()` and `HomePlugin.messages()`.

## Commands

| Command | Purpose |
|---------|---------|
| `/sethome <name>` | Create or move a home |
| `/home <name>` | Teleport to a home |
| `/deletehome <name>` | Delete a home (`delhome`, `dh`) |
| `/homes` | Open the homes GUI |
| `/renamehome <name> <new>` | Rename a home |
| `/movehome <name>` | Move a home to your location |
| `/edithome <name>` | Open the manage-home GUI |
| `/homeinfo <name>` | Print home details |
| `/adminhomes [player]` | Admin GUI (`home.admin`) |
| `/homeplugin reload` | Reload config/messages |
| `/homeplugin admin [player]` | Same as `/adminhomes` |
