# Use the official Gradle image with JDK 21
FROM gradle:8.7.0-jdk21 AS builder

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . .

RUN gradle shadowJar --no-daemon

FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /data

# Copy the built jar file from the builder stage
COPY --from=builder /home/gradle/src/build/libs/*.jar /data/server.jar

# Copy all files from the template directory to the working directory
COPY template /data

# Set the environment variable
ENV ONLINE_MODE=false

# Expose the port the server will be running on
EXPOSE 25565

# Define the entry point to run the jar
ENTRYPOINT ["java", "-jar", "server.jar"]
