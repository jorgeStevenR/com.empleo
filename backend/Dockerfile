# Etapa 1: Compilación con Maven y JDK 21
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -q clean package -DskipTests

# Etapa 2: Imagen final para ejecución
FROM eclipse-temurin:21-jdk-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar /app/portal_empleos.jar

# Configuración de variables y puerto
ENV JAVA_OPTS=""
EXPOSE 8080

# Comando de ejecución (Render usa la variable $PORT)
CMD ["sh", "-c", "java $JAVA_OPTS -jar -Dserver.port=${PORT:-8080} /app/portal_empleos.jar"]
