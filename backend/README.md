Quick start (development, H2 in-memory)

Requirements
- Java 17+
- Maven

Run locally (H2)

1. Build and run

   cd backend
   mvn spring-boot:run

2. Server

   The application will listen on port 8080 by default.

Run locally against the Docker Compose MySQL (convenience)

If you prefer to run the backend locally but use the MySQL instance provided by docker-compose, follow these steps:

1. Start only the DB container (from repo root):

   docker compose up -d db

2. Start the backend using the helper script (it sets the correct environment variables):

   cd backend
   ./run-with-mysql.sh

This will run the backend with the following connection settings by default:
- host: localhost
- port: 3306
- database: madfood
- user: madfood
- password: madfoodpwd

Note: the helper script assumes you have docker compose running the MySQL database and that the DB is reachable at localhost:3306. If you changed ports or credentials in docker-compose.yml, update the script accordingly.

Enable dev users (optional)

For convenience, the app can create demo users at startup (demo / demo123 and admin / admin123). This is NOT enabled by default. To enable it, set the environment variable or property:

- environment variable: APP_CREATE_DEV_USERS=true
- or in application.properties: app.create-dev-users=true

Example (Unix):

APP_CREATE_DEV_USERS=true mvn spring-boot:run

API endpoints (use API base: http://localhost:8080)
- POST /api/auth/register — register user (JSON body: {username, password})
- POST /api/auth/login — login and receive JWT (JSON body: {username, password})
- POST /api/delivery/partners/apply — delivery partner application (multipart/form-data). Public by design.
- GET  /api/delivery/partners/files/{filename} — download uploaded file (requires ROLE_ADMIN)

