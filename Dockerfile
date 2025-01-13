FROM gradle:jdk21-corretto-al2023 AS build
COPY --chown=gradle:gradle . /app
WORKDIR /app

RUN gradle jvmJar --no-daemon

FROM amazoncorretto:21.0.5-al2023-headless AS fama
COPY --from=build /app/build/libs/fama-jvm-*.jar /fama.jar

ENTRYPOINT ["/fama.jar"]
