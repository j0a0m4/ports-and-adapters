FROM eclipse-temurin:19.0.2_7-jdk-alpine as GRADLE
COPY *.gradle gradle.* gradlew /src/
COPY gradle /src/gradle
WORKDIR /src
RUN ["./gradlew", "--version"]

FROM GRADLE as BUILDER
VOLUME /tmp
WORKDIR /src
COPY . .
RUN [ "./gradlew", "build", "--exclude-task", "test", "--stacktrace", "--no-daemon", "--parallel"]

FROM eclipse-temurin:19.0.2_7-jre-alpine
COPY --from=BUILDER ports-and-adapters-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]