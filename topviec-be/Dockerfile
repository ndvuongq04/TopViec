# ====== Stage 1: Build ======
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

# Copy Gradle wrapper and build files first to reuse Docker cache.
COPY gradlew gradlew.bat settings.gradle build.gradle ./
COPY gradle/ gradle/

RUN chmod +x gradlew

# Download dependencies in a separate layer. This can fail before source is copied
# when no tasks need resolution yet, so keep the build resilient.
RUN ./gradlew dependencies --no-daemon || true

COPY src/ src/

RUN ./gradlew bootJar --no-daemon -x test

# ====== Stage 2: Run ======
FROM eclipse-temurin:21-jre
WORKDIR /app

RUN mkdir -p /app/uploads

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
