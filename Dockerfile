FROM gradle:jdk21-corretto-al2023 AS build

ARG SUPPORT_URL
ENV SUPPORT_URL=$SUPPORT_URL

ARG RABBIT_MQ_STOMP_URL
ENV RABBIT_MQ_STOMP_URL=$RABBIT_MQ_STOMP_URL

ARG RABBIT_MQ_STOMP_USERNAME
ENV RABBIT_MQ_STOMP_USERNAME=$RABBIT_MQ_STOMP_USERNAME

ARG RABBIT_MQ_STOMP_PASSWORD
ENV RABBIT_MQ_STOMP_PASSWORD=$RABBIT_MQ_STOMP_PASSWORD

COPY --chown=gradle:gradle . /app
WORKDIR /app

RUN gradle jvmJar --no-daemon

FROM amazoncorretto:21.0.5-al2023-headless AS fama

LABEL \
    maintainer="Nedeco" \
    org.opencontainers.image.title="Fama" \
    org.opencontainers.image.description="Fama integrates Smart City systems from the German city of Solingen into smart home environments." \
    org.opencontainers.image.authors="Nedeco"

COPY --from=build /app/build/libs/fama-jvm-*.jar /fama.jar

ENTRYPOINT ["java", "-jar", "/fama.jar"]
