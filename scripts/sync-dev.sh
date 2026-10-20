#!/usr/bin/env bash
# Compare each monorepo module jar to a Dev server.
# Only considers jars that already exist on the server (does not create new ones).
# Rebuilds when sources are newer than the local jar, then uploads if checksum differs.
set -euo pipefail

# --- configured ---
SSH_HOST="HomeServer"
REMOTE_BASE="/home/sanamo/KarterSanamo/servers"
REMOTE_PLUGINS="plugins"
MVN_ARGS=(-DskipTests)

# Reuse one SSH connection; avoid hanging forever on a dead transfer.
SSH_OPTS=(
  -o BatchMode=yes
  -o ConnectTimeout=15
  -o ServerAliveInterval=5
  -o ServerAliveCountMax=3
  -o ControlMaster=auto
  -o ControlPersist=60
)
SSH_CONTROL_PATH=""

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"
cd "${ROOT_DIR}"

ssh_cmd() {
  ssh "${SSH_OPTS[@]}" -o "ControlPath=${SSH_CONTROL_PATH}" "$@"
}

scp_cmd() {
  scp "${SSH_OPTS[@]}" -o "ControlPath=${SSH_CONTROL_PATH}" "$@"
}

init_ssh_multiplex() {
  SSH_CONTROL_PATH="$(mktemp -u /tmp/factions-sync-dev.XXXXXX)"
  ssh_cmd "${SSH_HOST}" "true"
}

close_ssh_multiplex() {
  if [[ -n "${SSH_CONTROL_PATH}" ]]; then
    ssh_cmd -O exit "${SSH_HOST}" >/dev/null 2>&1 || true
    rm -f "${SSH_CONTROL_PATH}"
    SSH_CONTROL_PATH=""
  fi
}

usage() {
  echo "Usage: $(basename "$0") [1|2|3]"
  echo "  Syncs changed monorepo modules to dev-1 / dev-2."
}

server_dir_for() {
  case "$1" in
    1) echo "dev-1" ;;
    2) echo "dev-2" ;;
    *) return 1 ;;
  esac
}

prompt_server() {
  local input=""
  while true; do
    read -r -p "Dev server number (1-3): " input
    case "${input}" in
      1|2|3) echo "${input}"; return ;;
      *) echo "Please enter 1, 2, or 3." >&2 ;;
    esac
  done
}

resolve_server() {
  local arg="${1:-}"
  if [[ -n "${arg}" ]]; then
    case "${arg}" in
      -h|--help) usage; exit 0 ;;
      1|2|3) echo "${arg}"; return ;;
      *) echo "Invalid server '${arg}'. Expected 1, 2, or 3." >&2; usage >&2; exit 1 ;;
    esac
  fi
  prompt_server
}

module_final_name() {
  local pom="$1/pom.xml"
  [[ -f "${pom}" ]] || return 1
  local name
  name="$(sed -n 's/.*<finalName>\([^<]*\)<\/finalName>.*/\1/p' "${pom}" | head -n1)"
  [[ -n "${name}" ]] || return 1
  printf '%s\n' "${name}"
}

list_modules() {
  sed -n 's/.*<module>\([^<]*\)<\/module>.*/\1/p' pom.xml
}

file_md5() {
  local path="$1"
  if [[ ! -f "${path}" ]]; then
    return 1
  fi
  if command -v md5 >/dev/null 2>&1; then
    md5 -q "${path}"
  else
    md5sum "${path}" | awk '{print $1}'
  fi
}

# True if local jar is missing, or any pom.xml / src file is newer than the jar.
needs_rebuild() {
  local module_dir="$1"
  local jar_path="$2"
  [[ -f "${jar_path}" ]] || return 0

  local newest
  newest="$(find "${module_dir}/pom.xml" "${module_dir}/src" -type f -newer "${jar_path}" 2>/dev/null | head -n1)"
  [[ -n "${newest}" ]]
}

# Args: remote_dir, then jar basenames (no .jar).
# Prints "name md5" for jars that exist, or "name MISSING".
fetch_remote_md5s() {
  local remote_dir="$1"
  shift
  ssh_cmd "${SSH_HOST}" bash -s -- \
    "${remote_dir}" "${REMOTE_PLUGINS}" "$@" <<'EOF'
remote_dir="$1"
plugins="$2"
shift 2
plugins_dir="${remote_dir}/${plugins}"
for name in "$@"; do
  jar="${plugins_dir}/${name}.jar"
  if [[ -f "${jar}" ]]; then
    echo "${name} $(md5sum "${jar}" | awk '{print $1}')"
  else
    echo "${name} MISSING"
  fi
done
EOF
}

map_get() {
  local haystack="$1"
  local needle="$2"
  local line key
  while IFS= read -r line || [[ -n "${line}" ]]; do
    [[ -z "${line}" ]] && continue
    key="${line%% *}"
    if [[ "${key}" == "${needle}" && "${key}" != "${line}" ]]; then
      printf '%s\n' "${line#* }"
      return 0
    fi
  done <<EOF
${haystack}
EOF
  return 1
}

# Batch-upload jars to a remote staging dir, then rename into place in one SSH.
# Avoids opening a new SSH session per jar (which often hangs after many uploads).
upload_jars_batch() {
  local remote_dir="$1"
  shift
  local -a jar_paths=("$@")
  local plugins_dir="${remote_dir}/${REMOTE_PLUGINS}"
  local staging="${plugins_dir}/.sync-tmp-$$"

  echo "  staging ${#jar_paths[@]} jar(s)..."
  ssh_cmd "${SSH_HOST}" "mkdir -p '${staging}'"
  scp_cmd -q "${jar_paths[@]}" "${SSH_HOST}:${staging}/"

  echo "  installing into ${REMOTE_PLUGINS}/..."
  ssh_cmd "${SSH_HOST}" bash -s -- "${staging}" "${plugins_dir}" <<'EOF'
staging="$1"
plugins_dir="$2"
for jar in "${staging}"/*.jar; do
  [ -e "${jar}" ] || continue
  mv -f "${jar}" "${plugins_dir}/$(basename "${jar}")"
done
rmdir "${staging}" 2>/dev/null || rm -rf "${staging}"
EOF
}

main() {
  local server_num remote_name remote_dir
  server_num="$(resolve_server "${1:-}")"
  remote_name="$(server_dir_for "${server_num}")"
  remote_dir="${REMOTE_BASE}/${remote_name}"

  echo "==> Target: ${SSH_HOST}:${remote_dir}/${REMOTE_PLUGINS}"

  init_ssh_multiplex
  trap close_ssh_multiplex EXIT

  if ! ssh_cmd "${SSH_HOST}" "test -d '${remote_dir}/${REMOTE_PLUGINS}'"; then
    echo "Remote plugins directory not found: ${remote_dir}/${REMOTE_PLUGINS}" >&2
    exit 1
  fi

  local -a modules=()
  local -a jar_names=()
  local module final_name
  while IFS= read -r module; do
    [[ -n "${module}" ]] || continue
    if ! final_name="$(module_final_name "${module}")"; then
      echo "  skip  ${module} (no finalName)"
      continue
    fi
    modules[${#modules[@]}]="${module}"
    jar_names[${#jar_names[@]}]="${final_name}"
  done < <(list_modules)

  echo "==> Fetching remote jar checksums..."
  local remote_md5s
  remote_md5s="$(fetch_remote_md5s "${remote_dir}" "${jar_names[@]}")"

  local -a build_modules=()
  local -a upload_modules=()
  local -a upload_jars=()
  local i=0 jar_path local_md5 remote_md5

  echo "==> Comparing local jars to server..."
  while [[ "${i}" -lt "${#modules[@]}" ]]; do
    module="${modules[$i]}"
    final_name="${jar_names[$i]}"
    jar_path="${module}/target/${final_name}.jar"
    remote_md5="$(map_get "${remote_md5s}" "${final_name}" || true)"

    if [[ "${remote_md5}" == "MISSING" || -z "${remote_md5}" ]]; then
      echo "  skip  ${module} -> ${final_name}.jar (not on server)"
      i=$((i + 1))
      continue
    fi

    if needs_rebuild "${module}" "${jar_path}"; then
      if [[ ! -f "${jar_path}" ]]; then
        echo "  build ${module} -> ${final_name}.jar (local jar missing)"
      else
        echo "  build ${module} -> ${final_name}.jar (sources newer)"
      fi
      build_modules[${#build_modules[@]}]="${module}"
      upload_modules[${#upload_modules[@]}]="${module}"
      upload_jars[${#upload_jars[@]}]="${final_name}"
      i=$((i + 1))
      continue
    fi

    local_md5="$(file_md5 "${jar_path}")"
    if [[ "${local_md5}" != "${remote_md5}" ]]; then
      echo "  upload ${module} -> ${final_name}.jar (checksum differs)"
      upload_modules[${#upload_modules[@]}]="${module}"
      upload_jars[${#upload_jars[@]}]="${final_name}"
    else
      echo "  ok     ${module} -> ${final_name}.jar"
    fi
    i=$((i + 1))
  done

  if [[ "${#build_modules[@]}" -eq 0 && "${#upload_modules[@]}" -eq 0 ]]; then
    echo "==> All modules match ${remote_name}. Nothing to do."
    exit 0
  fi

  if [[ "${#build_modules[@]}" -gt 0 ]]; then
    local module_csv=""
    i=0
    while [[ "${i}" -lt "${#build_modules[@]}" ]]; do
      if [[ -n "${module_csv}" ]]; then
        module_csv="${module_csv},"
      fi
      module_csv="${module_csv}${build_modules[$i]}"
      i=$((i + 1))
    done
    echo "==> Building ${#build_modules[@]} module(s): ${module_csv}"
    mvn -pl "${module_csv}" package "${MVN_ARGS[@]}"
  fi

  if [[ "${#upload_modules[@]}" -gt 0 ]]; then
    echo "==> Uploading ${#upload_modules[@]} jar(s)..."
    local -a jar_paths=()
    i=0
    while [[ "${i}" -lt "${#upload_modules[@]}" ]]; do
      module="${upload_modules[$i]}"
      final_name="${upload_jars[$i]}"
      jar_path="${module}/target/${final_name}.jar"
      if [[ ! -f "${jar_path}" ]]; then
        echo "Built jar not found: ${jar_path}" >&2
        exit 1
      fi
      echo "  ${final_name}.jar"
      jar_paths[${#jar_paths[@]}]="${jar_path}"
      i=$((i + 1))
    done
    upload_jars_batch "${remote_dir}" "${jar_paths[@]}"
    run_reboot "${remote_name}"
  fi

  echo "==> Done."
}

# /rb is a proxy command. Console must name the backend: /rb 1 dev-1
run_reboot() {
  local server="$1"
  echo "==> Running /rb 1 ${server}..."
  ssh_cmd "${SSH_HOST}" bash -s -- "${server}" <<'EOF'
server="$1"
if docker exec proxy sh -c 'command -v mc-send-to-console >/dev/null'; then
  docker exec proxy mc-send-to-console "rb 1 ${server}"
else
  echo "mc-send-to-console not found on proxy container." >&2
  exit 1
fi
EOF
}

main "$@"
