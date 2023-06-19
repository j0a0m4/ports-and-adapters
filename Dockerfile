FROM eclipse-temurin:19.0.2_7-jdk-alpine as gradle
RUN apk add --no-cache gradle

FROM gradle as builder
USER root
ENV APP_HOME=/builder
WORKDIR $APP_HOME
COPY . $APP_HOME
RUN gradle build --exclude-task test --stacktrace --no-daemon --no-build-cache --parallel

FROM eclipse-temurin:19.0.2_7-jre-alpine
WORKDIR /app
expose 8080
COPY --from=builder "/builder/build/libs/ports-and-adapters-0.0.1-SNAPSHOT.jar" .
CMD ["java", "-jar", "ports-and-adapters-0.0.1-SNAPSHOT.jar"]