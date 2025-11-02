FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
RUN mvn -B -q clean package -DskipTests
FROM openjdk:21-jdk-slim
WORKDIR /app
COPY --from=build /app/target/*.jar /app/portal_empleos.jar
ENV JAVA_OPTS=""
CMD ["sh", "-c", "java $JAVA_OPTS -jar -Dserver.port=${PORT:-8080} /app/portal_empleos.jar"]
