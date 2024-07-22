# Use the official Gradle image with JDK 21
FROM gradle:8.7.0-jdk21 AS builder

# Set the working directory inside the container
WORKDIR /data

# Copy the project files to the working directory
COPY . .

# Run the Gradle build command
RUN gradle build

# Use a new, smaller image for the runtime with JDK 21
FROM openjdk:21-jdk-slim

# Set the working directory inside the container
WORKDIR /data

# Copy the built jar file from the builder stage
COPY --from=builder /data/build/libs/*.jar /data/server.jar

# Copy all files from the template directory to the working directory
COPY template /data/template

# Set the environment variable
ENV ONLINE_MODE=false

# Expose the port the server will be running on
EXPOSE 25565

# Define the entry point to run the jar
ENTRYPOINT ["java", "-jar", "/data/server.jar"]
