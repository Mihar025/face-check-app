# ---------- STAGE 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build

# Кешируем зависимости
COPY pom.xml .
COPY ApiModule/pom.xml ApiModule/
COPY ConfigurationsModule/pom.xml ConfigurationsModule/
COPY SecurityDomainModule/pom.xml SecurityDomainModule/
COPY AppStarterModule/pom.xml AppStarterModule/
RUN mvn -q -DskipTests dependency:go-offline

# Копируем исходники и собираем только рантабельный модуль
COPY . .
# ВАЖНО: пропускаем компиляцию тестов во всех модулях
RUN mvn -q -Dmaven.test.skip=true -pl AppStarterModule -am package

# ---------- STAGE 2: runtime ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
RUN apt-get update -y && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
ENV JAVA_OPTS="-XX:+UseG1GC -XX:MaxRAMPercentage=70 -XX:MinRAMPercentage=20 -XX:InitialRAMPercentage=20 -XX:MaxMetaspaceSize=128m -XX:ReservedCodeCacheSize=96m -XX:+ExitOnOutOfMemoryError -Duser.timezone=America/New_York"
COPY --from=build /build/AppStarterModule/target/*.jar app.jar
EXPOSE 8088
HEALTHCHECK --interval=30s --timeout=3s --retries=5 CMD curl -fsS http://localhost:8088/api/v1/actuator/health || exit 1
ENTRYPOINT ["sh","-c","java $JAVA_OPTS -jar app.jar"]
