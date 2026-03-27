FROM eclipse-temurin:25-jdk-alpine AS builder
ARG MODULE_NAME
WORKDIR /build

COPY gradlew .
COPY gradle gradle
COPY settings.gradle .
COPY user-service/build.gradle user-service/build.gradle
COPY noti-service/build.gradle noti-service/build.gradle
COPY shared-lib/build.gradle shared-lib/build.gradle
RUN ./gradlew dependencies  --no-daemon -x test

COPY shared-lib/src shared-lib/src
COPY ${MODULE_NAME}/src ${MODULE_NAME}/src
RUN ./gradlew :${MODULE_NAME}:bootJar --no-daemon -x test

FROM eclipse-temurin:25-jdk-alpine AS extract
ARG MODULE_NAME
WORKDIR /extract

COPY --from=builder /build/${MODULE_NAME}/build/libs/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination out

FROM eclipse-temurin:25-jdk-alpine
WORKDIR /app
ARG EXPOSED_PORT
EXPOSE ${EXPOSED_PORT}
COPY --from=extract /extract/out/dependencies ./
COPY --from=extract /extract/out/spring-boot-loader ./
COPY --from=extract /extract/out/snapshot-dependencies ./
COPY --from=extract /extract/out/application ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]