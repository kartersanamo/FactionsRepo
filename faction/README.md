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
- All player text lives in `plugins/Faction/messages.yml` (Saber/FactionsUUID-style `&e[&6Factions&e]` prefix). Edit freely; `/f reload` reloads it.
- Claim protection (bypass via `/f bypass`).
- Autoclaim, faction/ally chat, and fly-in-claims via `FactionListener`.

## Messaging API

Commands own player messages. Mutation APIs on `FactionSystem` return `FactionResult`; subcommands map each outcome to a `messages.yml` key. Query helpers (`getFaction`, `hasInvite`, `isValidFactionName`, etc.) stay boolean/`null`.

```java
FactionResult result = factionSystem.setHome(player);
switch (result) {
    case SUCCESS:
        msg(player, "home.set");
        break;
    case NOT_YOUR_CLAIM:
        msg(player, "home.not-your-claim");
        break;
    // ...
}

// Listeners / non-command code may still use:
factionSystem.getMessages().send(player, "claim.protected");
factionSystem.broadcastToFaction(faction, "announce.format", "player", name, "message", text);
```

Placeholders use `%name%` in YAML. `%prefix%` is always injected.

## Commands

Membership: `create`, `join`, `leave`, `disband`, `invite`, `alt`, `invites`, `deinvite`, `open`, `roster`, `reserve`

Roles: `kick`, `ban`, `promote`, `coleader`, `mod`, `owner`, `ownerlist`, `defaultrank`, `title`, `perms`

Relations: `ally`, `enemy`, `truce`, `peaceful`

Relation colors (via `FactionSystem.getRelation` / `getRelationColor`): self green, ally light purple, truce aqua, enemy red, neutral white.

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
| `systems` | `FactionSystem` facade (public API) delegating to the subsystems below |

`systems` subsystems (each takes a `FactionRegistry`):

| Class | Purpose |
|-------|---------|
| `FactionRegistry` | Shared state (factions, claims, invites, reserved names), lookups, persistence, broadcasts |
| `MembershipService` | Create/leave/disband, invites, join, kick/ban, open, description, rules, rename |
| `RoleService` | Default rank, titles, promote, set role, transfer leadership |
| `RelationService` | Ally/enemy/truce relations, peaceful, permanent, grace |
| `ClaimService` | Claim/unclaim, unclaim all, base claim, `canBuild` |
| `HomeWarpService` | Home, warps, wild/stuck/corner teleport locations |
| `EconomyService` | Money, TNT, points, power boost, strikes, max vaults |
