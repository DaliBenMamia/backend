FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

COPY pom.xml pom.xml
COPY salon-service/pom.xml salon-service/pom.xml
COPY salon-service/.mvn salon-service/.mvn
COPY salon-service/mvnw salon-service/mvnw
COPY salon-service/mvnw.cmd salon-service/mvnw.cmd
COPY salon-service/src salon-service/src

RUN chmod +x salon-service/mvnw && ./salon-service/mvnw -f salon-service/pom.xml -q -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/salon-service/target/salon-service-*.jar app.jar

EXPOSE 8894

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
