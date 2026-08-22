FROM maven:3.9.4-eclipse-temurin-21 AS build

WORKDIR /workspace

COPY pom.xml ./
COPY src ./src

RUN mvn -DskipTests package -DskipITs -B -V


FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=build /workspace/target/finance-api-0.1.0-SNAPSHOT.jar app.jar

COPY docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

EXPOSE 8082

ENTRYPOINT ["/docker-entrypoint.sh"]
