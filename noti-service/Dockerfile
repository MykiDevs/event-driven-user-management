FROM eclipse-temurin:25-jdk-alpine AS builder
WORKDIR /app
COPY gradlew .
COPY gradlew.bat .
COPY gradle/ gradle/
COPY settings.gradle .
COPY gradle.properties .

COPY shared-lib/build.gradle shared-lib/build.gradle
COPY user-service/build.gradle user-service/build.gradle

COPY noti-service/build.gradle noti-service/build.gradle

RUN chmod +x gradlew
RUN ./gradlew :noti-service:dependencies --no-daemon

COPY shared-lib/src shared-lib/src
COPY noti-service/src noti-service/src

RUN ./gradlew :noti-service:bootJar --no-daemon -x test

FROM eclipse-temurin:25-jre-alpine

COPY --from=builder /app/noti-service/build/libs/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8081