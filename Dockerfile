FROM maven:3.10-eclipse-temurin-25 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -q -DskipTests package \
    && mv target/budgetflow-0.0.1-SNAPSHOT.jar app.jar

FROM eclipse-temurin:25-jre-jammy
WORKDIR /app
RUN useradd --system --uid 1001 app
COPY --from=build /app/app.jar app.jar
USER 1001
EXPOSE 6060
ENTRYPOINT ["java", "-jar", "app.jar"]
