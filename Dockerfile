# Stage 1: Build
FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN gradle shadowJar --no-daemon

# Debugging: List the contents of the build/libs directory
RUN ls -l /home/gradle/src/build/libs/

# Stage 2: Final Image
FROM openjdk:21-jdk-slim

WORKDIR /data

# Copy the built jar file from the builder stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /data/server.jar

# Debugging: List the contents of the /data directory to verify the jar file is copied
RUN ls -l /data/

COPY template /data

ENV ONLINE_MODE=false

EXPOSE 25565

ENTRYPOINT ["java", "-jar", "server.jar"]
