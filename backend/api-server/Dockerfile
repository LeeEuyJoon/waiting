# Stage 1: 빌드
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
COPY . .
RUN chmod +x gradlew && ./gradlew :api-server:bootJar --no-daemon

# Stage 2: 실행
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/api-server/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]