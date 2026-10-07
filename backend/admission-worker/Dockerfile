FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :admission-worker:bootJar --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/admission-worker/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]