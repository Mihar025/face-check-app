# ===== Build =====
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /src

# parent pom (на уровень выше), чтобы mvn видел родителя
COPY ../pom.xml ../pom.xml
# pom текущего модуля
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# код модуля
COPY . .
RUN mvn -q -DskipTests package

# ===== Run =====
FROM eclipse-temurin:21-jre
WORKDIR /app
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=75 -Duser.timezone=America/New_York"
COPY --from=build /src/target/*.jar app.jar

# твой прод-порт
EXPOSE 8088

# важный момент: health под контекстом /api/v1
HEALTHCHECK --interval=30s --timeout=3s --retries=5 \
  CMD curl -fsS http://localhost:8088/api/v1/actuator/health || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
