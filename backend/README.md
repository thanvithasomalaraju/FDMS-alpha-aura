# Delivery Backend — README

This directory contains the Spring Boot delivery backend used for the MadFood project. It is configured for local development with H2 by default and supports running in Docker Compose with MySQL for persistence.

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

3. Enable dev users (optional)

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

File uploads
- Files are saved to the path configured by the `file.upload-dir` property (default: ./uploads relative to backend directory).
- Multipart limits are configured in application.properties (5MB per file, 20MB request).

Security and production notes
- By default the app uses H2 in-memory DB. To use MySQL, set SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD.
- Replace the placeholder JWT secret by setting environment variable `JWT_SECRET` to a secure random string (32+ chars) before production.
- Dev user creation is disabled by default. If you enable it for local testing, remove or rotate those credentials before production.
- File serving endpoint is restricted to ROLE_ADMIN, but for production it is recommended to store uploads in S3/GCS and serve presigned URLs instead of serving files from the app.
- Rate limiting is an in-memory limiter intended only for staging — use a distributed rate limiter (API gateway or Redis-backed) for production.

Docker Compose (MySQL)

- A docker-compose.yml is provided at the repo root. It starts MySQL and the backend.

  docker compose up --build

- The compose file will bind-mount ./backend/uploads for file persistence and uses example MySQL credentials (see docker-compose.yml). Replace secrets before deploying.

Configuration (env var overrides)
- SPRING_DATASOURCE_URL, SPRING_DATASOURCE_USERNAME, SPRING_DATASOURCE_PASSWORD
- JWT_SECRET
- FILE_UPLOAD_DIR (path where uploads are stored)
- APP_ALLOWED_ORIGINS (CORS allowed origins, comma-separated)
- APP_CREATE_DEV_USERS=true (enable demo users on startup)

Logging
- Logs are output at INFO level by default. Check application.properties to adjust logging levels.

Support
- See PRODUCTION-README.md for the production checklist and hardening guidance.
