# Stage 1: build
# Start with a Maven image that includes JDK 21
FROM maven:3.9.8-amazoncorretto-21 AS build

# Copy source code and pom.xml file to /app folder
WORKDIR /app
COPY pom.xml .
COPY src ./src

# Build source code with maven
RUN mvn package -DskipTests

#Stage 2: create image
# Start with Amazon Correto JDK 21
FROM amazoncorretto:21.0.4

# Set working folder to App and copy complied file from above step
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

#Stage 3: Run image
# docker build -t identity-service:0.0.1 .

#Stage 4: Run container
# docker run
#    --name identity-service
#    -p 8080:8080
#    -e DBMS_CONNECTION=jdbc:mysql://172.17.0.2:3306/identity_service
#    identity-service:0.0.1