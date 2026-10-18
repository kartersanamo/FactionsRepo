# faction

Core factions plugin: faction entities, players, claims, and `/f` commands.

## Build

```bash
mvn -pl faction -am package
```

Install `faction/target/faction-1.0-SNAPSHOT.jar`. ACF is shaded from the parent POM (`com.kartersanamo.libs.aikar`).

Main class: `FactionPlugin` (`plugin.yml`).

## Runtime

- Factions persist to `plugins/Faction/factions.yml` (load on enable, save on disable and after changes).
- Claim protection blocks break/place/interact in foreign claims.

## Working commands

| Command | Description |
|---------|-------------|
| `/f create <name>` | Create a faction |
| `/f leave` | Leave (non-leaders) |
| `/f disband` | Disband (leader) |
| `/f invite <player>` | Invite a player (leader) |
| `/f join <faction>` | Accept an invite |
| `/f kick <player>` | Kick a member (leader) |
| `/f show\|who [faction\|player]` | Show faction info |
| `/f claim` | Claim the chunk you stand in |
| `/f unclaim` | Unclaim your chunk |

## Commands (package layout)

`FactionSubCommand` (`commands`) is the ACF base. Subcommands are grouped by category:

| Package | Purpose |
|---------|---------|
| `commands.faction.membership` | Create, join, leave, invites, roster, open/reserve |
| `commands.faction.role` | Ranks, promote, kick/ban, permissions, titles |
| `commands.faction.relation` | Ally, enemy, truce, peaceful |
| `commands.faction.claim` | Claim/unclaim, map, seechunk, corner |
| `commands.faction.teleport` | Homes, warps, stuck, wild, fly, coords, near |
| `commands.faction.economy` | Money, vaults, chest, TNT, power, boosters, upgrades |
| `commands.faction.info` | Show, list, top, help, chat, tag, rules, logs |
| `commands.faction.admin` | Reload, save, config, debug, bypass, strikes, grace |
