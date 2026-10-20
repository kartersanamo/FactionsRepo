#!/usr/bin/env bash
# Host-side watcher for Network reboot requests.
# Reads allowlisted *.request files written by the proxy plugin and restarts
# only the matching Docker Compose service. Does not expose docker.sock to the proxy.
set -euo pipefail

ALLOWED_SERVICES="dev-1 dev-2"
COMPOSE_DIR="${COMPOSE_DIR:-${HOME}/Factions}"
COMPOSE_FILE="${COMPOSE_FILE:-${COMPOSE_DIR}/docker-compose.yml}"
REQUEST_DIR="${REQUEST_DIR:-${HOME}/Factions/proxy-plugins/Network/restart-requests}"

mkdir -p "${REQUEST_DIR}"

is_allowed() {
    local service="$1"
    local allowed
    for allowed in ${ALLOWED_SERVICES}; do
        if [ "${allowed}" = "${service}" ]; then
            return 0
        fi
    done
    return 1
}

echo "Watching ${REQUEST_DIR} for restart requests"

while true; do
    shopt -s nullglob
    for request in "${REQUEST_DIR}"/*.request; do
        service="$(basename "${request}" .request)"
        if [[ ! "${service}" =~ ^[a-zA-Z0-9][a-zA-Z0-9_-]*$ ]]; then
            echo "Ignoring invalid restart request name: ${service}"
            rm -f "${request}"
            continue
        fi
        if ! is_allowed "${service}"; then
            echo "Ignoring non-allowlisted restart request: ${service}"
            rm -f "${request}"
            continue
        fi
        echo "Restarting compose service ${service}"
        rm -f "${request}"
        docker compose -f "${COMPOSE_FILE}" restart "${service}"
    done
    sleep 1
done
