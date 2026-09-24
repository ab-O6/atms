#!/usr/bin/env bash
# Run backend integration tests with Docker available locally or over SSH context.
set -euo pipefail

ROOT="$(cd "$(dirname "$0")/.." && pwd)"
cd "$ROOT/backend"

export DOCKER_API_VERSION="${DOCKER_API_VERSION:-1.44}"

DOCKER_CTX="$(docker context show 2>/dev/null || echo default)"
DOCKER_HOST_RAW="$(docker context inspect "$DOCKER_CTX" --format '{{.Endpoints.docker.Host}}' 2>/dev/null || true)"

if [[ -n "${ATMS_IT_JDBC_URL:-}" ]]; then
  echo "[atms] Using external DB: ATMS_IT_JDBC_URL"
elif [[ "$DOCKER_HOST_RAW" == ssh://* ]]; then
  TUNNEL_PORT="${ATMS_DOCKER_TUNNEL_PORT:-2375}"
  export DOCKER_HOST="tcp://127.0.0.1:${TUNNEL_PORT}"
  SSH_TARGET="${DOCKER_HOST_RAW#ssh://}"
  if ! DOCKER_HOST="$DOCKER_HOST" docker info >/dev/null 2>&1; then
    echo "[atms] Opening SSH Docker tunnel to ${SSH_TARGET} on ${DOCKER_HOST} ..."
    ssh -f -N -L "127.0.0.1:${TUNNEL_PORT}:/var/run/docker.sock" "$SSH_TARGET"
    sleep 1
  fi
  echo "[atms] Testcontainers via ${DOCKER_HOST} (API ${DOCKER_API_VERSION})"
elif [[ -n "$DOCKER_HOST_RAW" && "$DOCKER_HOST_RAW" != unix://* ]]; then
  export DOCKER_HOST="$DOCKER_HOST_RAW"
fi

exec ./mvnw test "$@"
