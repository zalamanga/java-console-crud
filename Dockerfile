FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml ./
COPY .mvn ./.mvn
COPY src ./src
COPY database ./database
RUN --mount=type=cache,target=/root/.m2 mvn -B -q verify
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/target/java-console-crud.jar ./app.jar
ENTRYPOINT ["java", "-Xmx128m", "-jar", "app.jar"]
