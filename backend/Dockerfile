# =========================
# Etapa 1: Compilar el proyecto
# =========================
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -q clean package -DskipTests

# =========================
# Etapa 2: Ejecutar la app
# =========================
FROM openjdk:21-jdk-slim
WORKDIR /app

# Copiamos el JAR generado en target y lo dejamos con un nombre estable
COPY --from=build /app/target/*.jar /app/portal_empleos.jar

# Opcional: variables JVM (puedes ajustar memoria, etc.)
ENV JAVA_OPTS=""

# IMPORTANTE: no fijes el puerto; respeta ${PORT} (Render lo inyecta).
# En local: docker run -e PORT=8080 -p 8080:8080 imagen
CMD ["sh", "-c", "java $JAVA_OPTS -jar -Dserver.port=${PORT:-8080} /app/portal_empleos.jar"]
