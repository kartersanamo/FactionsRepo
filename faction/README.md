# faction

Core factions plugin: faction entities, players, claims, relations, and `/f` commands.

## Build

```bash
mvn -pl faction -am package
```

Install `faction/target/faction-1.0-SNAPSHOT.jar`. ACF is shaded from the parent POM (`com.kartersanamo.libs.aikar`).

Main class: `FactionPlugin` (`plugin.yml`).

## Runtime

- Persists to `plugins/Faction/factions.yml` (load on enable, save on disable and after changes).
- Claim protection (bypass via `/f bypass`).
- Autoclaim, faction/ally chat, and fly-in-claims via `FactionListener`.

## Commands

Membership: `create`, `join`, `leave`, `disband`, `invite`, `invites`, `deinvite`, `open`, `roster`, `reserve`

Roles: `kick`, `ban`, `promote`, `coleader`, `mod`, `owner`, `ownerlist`, `defaultrank`, `title`, `perms`

Relations: `ally`, `enemy`, `truce`, `peaceful`

Claims: `claim`, `unclaim`, `unclaimall`, `autoclaim`, `map`, `showclaims`, `seechunk`, `setbase`, `corner`, `safeunclaimall`, `warzoneunclaimall`

Teleport: `sethome`, `home`, `delhome`, `setwarp`, `warp`, `stuck`, `wild`, `fly`, `near`, `coords`

Economy: `money`, `power`, `powerboost`, `points`, `tnt`, `tntfill`, `vault`, `chest`, `viewchest`, `boosters`, `givebooster`, `upgrades`, `setmaxvaults`

Info: `show`/`who`, `help`, `list`, `top`, `desc`, `tag`, `rules`, `announce`, `chat`, `scoreboard`, `logins`, `logs`, `status`, `version`

Admin: `saveall`, `reload`, `bypass`, `chatspy`, `config`, `debug`, `grace`, `permanent`, `strikes`

Misc: `bundle`

## Package layout

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
| `commands.faction.misc` | Bundle |
