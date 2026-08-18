###############################################
# 1. BUILD-STAGE
#    Baut dein Spring-Boot-Projekt mit Maven
#    Vorteil: Du brauchst lokal kein JDK/Maven
###############################################
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Arbeitsverzeichnis im Build-Container
WORKDIR /build

# Nur pom.xml kopieren → Maven Dependencies werden gecached
COPY pom.xml .

# Dependencies herunterladen (Cache bleibt stabil)
RUN mvn -q dependency:go-offline

# Jetzt den Source-Code kopieren
COPY src ./src

# Projekt bauen (Tests optional überspringen)
RUN mvn -q -DskipTests package


###############################################
# 2. RUNTIME-STAGE
#    Minimaler, schneller Java-21 JRE Container
#    → kleiner, sicherer, schneller Start
###############################################
FROM eclipse-temurin:21-jre

# Non-root User für Sicherheit
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Arbeitsverzeichnis im Runtime-Container
WORKDIR /app

# Das fertige JAR aus dem Build-Container kopieren
COPY --from=build /build/target/*.jar app.jar

# Optional: JVM-Optimierungen für Container
# (Spring Boot + JVM Container Awareness)
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Startbefehl
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
