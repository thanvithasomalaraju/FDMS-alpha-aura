# Docker-compose quickstart

This repo includes a docker-compose.yml that starts:
- MySQL 8 (db)
- backend service (builds the Spring Boot delivery backend)

Quick start:
1. Build and start services
   docker compose up --build

2. The backend will be reachable at http://localhost:8080

3. Default MySQL credentials used by the compose file:
   - host: db (accessible from containers)
   - port: 3306
   - database: madfood
   - user: madfood
   - password: madfoodpwd

Notes:
- The backend stores uploaded files under ./backend/uploads on the host (bind mount).
- JWT secret and other sensitive values in docker-compose.yml are placeholders; replace them before deploying.
- To switch back to in-memory H2, unset SPRING_DATASOURCE_URL / related env vars.
