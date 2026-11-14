#!/usr/bin/env bash
# En Linux se utilizan Java 21 y Maven instalados por el usuario.
set -euo pipefail
cd "$(dirname "$0")/.."
exec mvn "$@"
