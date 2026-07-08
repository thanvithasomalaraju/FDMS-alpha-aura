# Docker/dev environment for the backend

This helper script is used as a convenience to create the uploads directory when running in some container runtimes.

#!/bin/sh
set -e

UPLOAD_DIR=${FILE_UPLOAD_DIR:-/app/uploads}
mkdir -p "$UPLOAD_DIR"
chown -R app:app "$UPLOAD_DIR" || true
exec "$@"
