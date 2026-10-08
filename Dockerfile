FROM eclipse-temurin:25-jdk-alpine AS build

WORKDIR /workspace

COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests \
    && cp target/hotel-0.1.0.jar app.jar

FROM eclipse-temurin:25-jre-alpine

RUN addgroup -S -g 10001 spring \
    && adduser -S -D -H -u 10001 -G spring spring

USER 10001:10001

WORKDIR /app

COPY --from=build --chown=spring:spring /workspace/app.jar app.jar

USER 10001:10001

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
