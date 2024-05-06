# syntax=docker/dockerfile:1

FROM --platform=$BUILDPLATFORM  eclipse-temurin:22_36-jdk-alpine as base
ARG TARGETPLATFORM
ARG BUILDPLATFORM
WORKDIR /app
RUN apk --no-cache add curl
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:resolve
COPY src ./src
COPY db ./db

FROM base as development
CMD ["./mvnw", "spring-boot:run", "-Dspring.profiles.active=dev", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]

FROM base as test
CMD ["./mvnw", "spring-boot:run", "-Dspring.profiles.active=test", "-Dspring-boot.run.jvmArguments='-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8000'"]

FROM base as build
RUN ./mvnw package

FROM eclipse-temurin:22_36-jre-alpine as production
EXPOSE 8080
COPY --from=build /app/target/flo-no-kanji-*.jar /flo-no-kanji.jar
CMD ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "/flo-no-kanji.jar"]
