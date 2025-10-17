# ---------- STAGE 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Cначала только poms, чтобы закешировать зависимости
COPY pom.xml .
COPY ApiModule/pom.xml ApiModule/
COPY ConfigurationsModule/pom.xml ConfigurationsModule/
COPY SecurityDomainModule/pom.xml SecurityDomainModule/
COPY AppStarterModule/pom.xml AppStarterModule/

RUN mvn -q -DskipTests dependency:go-offline

# Теперь весь код
COPY . .

# Собираем только рантабельный модуль и его зависимости
RUN mvn -q -DskipTests -pl AppStarterModule -am package

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app

# healthcheck утилита
RUN apt-get update -y && apt-get install -y curl && rm -rf /var/lib/apt/lists/*

ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=70 -XX:MinRAMPercentage=20 -XX:InitialRAMPercentage=20 -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=96m -XX:+ExitOnOutOfMemoryError -Duser.timezone=America/New_York"

# Копируем собранный jar
COPY --from=build /build/AppStarterModule/target/*.jar app.jar

EXPOSE 8088

# У тебя context-path /api/v1
HEALTHCHECK --interval=30s --timeout=3s --retries=5 \
  CMD curl -fsS http://localhost:8088/api/v1/actuator/health || exit 1

ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
