# network

BungeeCord/Waterfall proxy plugin: `/reboot` for `dev-1` and `dev-2`, plus a per-server player return queue.

This module does **not** talk to Docker. The proxy writes an allowlisted request file; a host-side watcher restarts the matching Compose service.

## Build

```bash
mvn -pl network -am package
```

Install `network/target/Network.jar` into the proxy plugin folder.

Main class: `com.kartersanamo.network.NetworkPlugin`.

Java 25, BungeeCord API `1.21-R0.4` (`provided`). Compatible with current BungeeCord/Waterfall command, scheduler, and server APIs.

## Command

`/reboot <time|cancel>` — permission `network.reboot`

| Example | Effect |
|---------|--------|
| `/reboot 30s` | Reboot the player's current backend in 30 seconds |
| `/reboot 5m` | Reboot in 5 minutes |
| `/reboot 1h` | Reboot in 1 hour |
| `/reboot cancel` | Cancel that backend's pending countdown |

The command uses the player's current Bungee server. It only works on `dev-1` and `dev-2`. Hub, the proxy lobby, and any other backend are rejected. Console cannot run it because there is no current backend.

Duplicate schedules on the same server are rejected. Cancel is ignored once the restart has started.

## Restart mechanism

1. After the countdown, players on that backend are queued by UUID (and original server) and sent to `hub`.
2. The plugin writes `plugins/Network/restart-requests/<service>.request` for the allowlisted Compose service only (`dev-1` → `dev-1`, `dev-2` → `dev-2`).
3. `network/scripts/restart-watcher.sh` runs **on the host**, not in the proxy container. It reads those files and runs `docker compose restart <service>`. The proxy never gets `/var/run/docker.sock`.
4. The plugin waits until that backend stops answering a Minecraft status ping, then until it answers again. A running container is not enough.
5. Queued players are sent back to the server they came from. Disconnecting does not drop the queue entry; logging back in while the server is still down keeps them on hub. If the backend never becomes ready, they stay on hub and are told so.

Do not rely on Docker `restart: unless-stopped` after a graceful Minecraft stop. `docker compose restart <service>` is the supported path.

## Runtime

- Config: `plugins/Network/config.yml`
- Messages: `plugins/Network/messages.yml`
- Queue: `plugins/Network/queues.yml`
- Restart requests: `plugins/Network/restart-requests/`
- Schedule requests: `plugins/Network/schedule-requests/<server>.request` (duration in the file; used by
  `scripts/sync-dev.sh`)

On a typical homeserver bind-mount that is:

`~/Factions/proxy-plugins/Network/` → `/plugins/Network/`

## Host watcher

Copy `network/scripts/restart-watcher.sh` to the host and run it as a long-lived process (systemd, tmux, or similar). Defaults:

| Variable | Default |
|----------|---------|
| `COMPOSE_DIR` | `$HOME/Factions` |
| `COMPOSE_FILE` | `$COMPOSE_DIR/docker-compose.yml` |
| `REQUEST_DIR` | `$HOME/Factions/proxy-plugins/Network/restart-requests` |

Only `dev-1` and `dev-2` are accepted. Anything else is deleted and ignored.

## Deploy from this machine

From the repo root:

```bash
mvn -pl network -am package
scp network/target/Network.jar USER@HOMESERVER:~/Factions/proxy-plugins/Network.jar
scp network/scripts/restart-watcher.sh USER@HOMESERVER:~/Factions/scripts/restart-watcher.sh
ssh USER@HOMESERVER 'chmod +x ~/Factions/scripts/restart-watcher.sh'
```

On the homeserver, start the watcher once (if it is not already running):

```bash
mkdir -p ~/Factions/proxy-plugins/Network/restart-requests
# example: systemd, tmux, or:
nohup env COMPOSE_DIR="$HOME/Factions" REQUEST_DIR="$HOME/Factions/proxy-plugins/Network/restart-requests" \
  ~/Factions/scripts/restart-watcher.sh >> ~/Factions/logs/restart-watcher.log 2>&1 &
```

Restart **only the proxy** Compose service so it loads the new JAR. Do not restart `hub`, `dev-1`, or `dev-2` for this deploy:

```bash
cd ~/Factions
docker compose restart PROXY_SERVICE
```

Replace `USER`, `HOMESERVER`, and `PROXY_SERVICE` with your SSH user, host, and the proxy service name in `docker-compose.yml`.

After the first boot, confirm Bungee `config.yml` still grants `network.reboot` to the admin group, and that the default/fallback server is `hub`.

## Testing checklist

- [ ] A player without `network.reboot` is denied.
- [ ] `/reboot 30s` on hub (or any non-dev server) is rejected.
- [ ] `/reboot 30s` on `dev-1` schedules only `dev-1`; `dev-2` and hub stay up.
- [ ] `/reboot 30s` on `dev-2` schedules only `dev-2`.
- [ ] Invalid / zero / huge values (`/reboot 0s`, `/reboot abc`, `/reboot 99h`) print usage.
- [ ] A second `/reboot` on the same server is rejected until cancel or completion.
- [ ] `/reboot cancel` during countdown stops announcements and does not restart.
- [ ] `/reboot cancel` after players have been moved to hub does not abort the restart.
- [ ] Players see the initial schedule, a one-minute warning when the wait is longer than a minute, a 10-second countdown, and a restart message — not a chat line every second of a long wait.
- [ ] At restart, players on that backend are sent to hub and their UUIDs are stored in `queues.yml` under the original server.
- [ ] A player who disconnects while waiting is still returned after they log back in, once that backend is ready.
- [ ] When the backend answers Minecraft pings again, queued players are sent to the server they came from (`dev-1` queue vs `dev-2` queue).
- [ ] If the watcher is stopped so the backend never returns, players stay on hub, get a failure message, and the plugin does not keep connecting forever.
- [ ] Proxy logs show schedule, cancel, restart request, ready/failure, and queue processing.
