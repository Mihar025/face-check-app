# ---------- STAGE 1: build ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY ApiModule/pom.xml ApiModule/
COPY ConfigurationsModule/pom.xml ConfigurationsModule/
COPY SecurityDomainModule/pom.xml SecurityDomainModule/
COPY AppStarterModule/pom.xml AppStarterModule/
RUN mvn dependency:go-offline -B
COPY . .
RUN mvn clean package -DskipTests -pl AppStarterModule -am -B

# ---------- STAGE 2: runtime ----------
FROM openjdk:21-jdk-slim AS runtime
WORKDIR /app

ARG JAR_FILE=AppStarterModule/target/AppStarterModule-0.0.1-SNAPSHOT.jar
COPY --from=build /build/${JAR_FILE} app.jar

# Лучше передавать все DB‑URL и креды извне, но можно задать дефолт:
ENV SPRING_DATASOURCE_URL=jdbc:postgresql://postgresql:5432/face_check
ENV SPRING_DATASOURCE_USERNAME=misha
ENV SPRING_DATASOURCE_PASSWORD=misha
ENV SPRING_PROFILES_ACTIVE=dev

EXPOSE 8088
ENTRYPOINT ["java", "-jar", "app.jar"]