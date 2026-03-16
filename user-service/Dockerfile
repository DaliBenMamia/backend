FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY .mvn .mvn
COPY mvnw mvnw
COPY mvnw.cmd mvnw.cmd
COPY pom.xml pom.xml
RUN chmod +x mvnw && ./mvnw -q -DskipTests dependency:go-offline

COPY src src
RUN ./mvnw -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8893

ENTRYPOINT ["java", "-jar", "app.jar"]
