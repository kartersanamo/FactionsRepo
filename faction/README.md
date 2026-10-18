# faction

Core factions plugin: faction entities, players, and `/f` commands.

## Build

```bash
mvn -pl faction -am package
```

Install `faction/target/faction-1.0-SNAPSHOT.jar`. ACF is shaded from the parent POM into each module jar (`com.kartersanamo.libs.aikar`).

Main class: `FactionPlugin` (`plugin.yml`).

## Commands

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
