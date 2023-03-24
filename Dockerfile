# Use Maven with OpenJDK 8 as the base image
FROM maven:3.8.4-openjdk-11 as build

# Set the working directory
WORKDIR /app

# Copy main pom
COPY pom.xml .
RUN mvn install -N -DskipTests

# Copy common files
COPY common/pom.xml common/
COPY common/src common/src/
RUN mvn install -DskipTests -f common/pom.xml

# Set the working directory for the service
ARG SERVICE_NAME
WORKDIR /app/${SERVICE_NAME}

# Copy the service-specific POM and source files
COPY ${SERVICE_NAME}/pom.xml .
COPY ${SERVICE_NAME}/src ./src

# Build the project inside the container
RUN mvn clean package -DskipTests

# Use OpenJDK 8 as the base image for the application
FROM openjdk:11-jdk-slim

# Set the working directory
ARG SERVICE_NAME
WORKDIR /app

# Copy the built JAR file from the build stage
COPY --from=build /app/${SERVICE_NAME}/target/*.jar app.jar

# Run the application with the dev profile
ENTRYPOINT ["sh", "-c", "sleep 30 && java -jar app.jar --spring.profiles.active=dev"]