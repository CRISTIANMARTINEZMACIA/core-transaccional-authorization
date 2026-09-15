FROM gradle:8.10-jdk17-alpine AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle ./gradle
COPY gradlew ./

RUN ./gradlew dependencies --no-daemon || return 0

COPY src ./src

RUN ./gradlew bootJar --no-daemon -x test

FROM eclipse-temurin:17-jre-alpine AS runtime

WORKDIR /app

RUN addgroup -S app && adduser -S app -G app
USER app:app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8083

ENTRYPOINT ["java", "-jar","app.jar"]