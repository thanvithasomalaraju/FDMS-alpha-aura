#!/bin/sh
set -e

# Run the backend (mvn) but point it at the local docker-compose MySQL instance
# Usage: ./run-with-mysql.sh
# Ensure `docker compose up -d db` has been run and MySQL is reachable on localhost:3306

export SPRING_DATASOURCE_URL="jdbc:mysql://localhost:3306/madfood?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC"
export SPRING_DATASOURCE_USERNAME="madfood"
export SPRING_DATASOURCE_PASSWORD="madfoodpwd"
export SPRING_JPA_HIBERNATE_DDL_AUTO="update"
export APP_CREATE_DEV_USERS="true"

echo "Starting backend with MySQL at localhost:3306 (user=madfood). Make sure docker compose up -d db has been run."

mvn spring-boot:run
