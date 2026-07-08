# Docker image for the delivery backend (multi-stage build)

FROM maven:3.8.8-eclipse-temurin-17 AS build
WORKDIR /app

# copy only the files we need for a reproducible build
COPY pom.xml ./
COPY src ./src

# build the application
RUN mvn -B -DskipTests package

# runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# copy built jar
COPY --from=build /app/target/delivery-backend-0.0.1-SNAPSHOT.jar /app/app.jar

# create uploads dir and expose it as a volume
RUN mkdir -p /app/uploads
VOLUME ["/app/uploads"]

# default envs (can be overridden by docker-compose or environment)
ENV SPRING_PROFILES_ACTIVE=docker
ENV FILE_UPLOAD_DIR=/app/uploads

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java -Dfile.upload-dir=${FILE_UPLOAD_DIR} -jar /app/app.jar"]
