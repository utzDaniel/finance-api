FROM maven:3.9.4-eclipse-temurin-21 AS build
WORKDIR /workspace
# copy pom and sources into build context; don't require mvnw (use container's mvn)
COPY pom.xml ./
COPY src ./src
RUN mvn -DskipTests package -DskipITs -B -V

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/finance-api-0.1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
