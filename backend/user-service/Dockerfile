###############################################
# 1. BUILD-STAGE
###############################################
FROM maven:3.9.6-eclipse-temurin-21 AS build

ARG GH_PACKAGES_TOKEN

WORKDIR /build

# Maven settings.xml für GitHub Packages
RUN mkdir -p /root/.m2 && \
    echo "<settings><servers><server><id>github</id><username>Roman-e-prog</username><password>${GH_PACKAGES_TOKEN}</password></server></servers></settings>" \
    > /root/.m2/settings.xml

COPY pom.xml .
RUN mvn -q dependency:go-offline

COPY src ./src
RUN mvn -q -DskipTests package


###############################################
# 2. RUNTIME-STAGE
###############################################
FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/target/*.jar app.jar

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

EXPOSE 8084

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
