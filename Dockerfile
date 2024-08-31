# Étape de construction
FROM maven:3.8.5-openjdk-8 AS build

WORKDIR /app

COPY pom.xml ./

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

# Étape de production
FROM openjdk:8-jre-alpine

WORKDIR /app

COPY --from=build /app/target/*.jar CyberNexus-1.0-SNAPSHOT.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/CyberNexus-1.0-SNAPSHOT.jar"]
