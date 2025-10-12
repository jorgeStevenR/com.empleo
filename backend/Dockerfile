# Etapa 1: Compilar el proyecto
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -q clean package -DskipTests

# Etapa 2: Ejecutar la app
FROM openjdk:21-jdk-slim
WORKDIR /app
# Copiamos el JAR generado en target y lo dejamos con un nombre estable
COPY --from=build /app/target/*.jar portal_empleos.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "portal_empleos.jar"]
