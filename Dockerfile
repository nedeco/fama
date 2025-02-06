FROM bellsoft/liberica-openjdk-alpine:21.0.6 AS fama

LABEL \
  maintainer="Nedeco" \
  org.opencontainers.image.title="Fama" \
  org.opencontainers.image.description="Fama integrates Smart City systems from the German city of Solingen into smart home environments." \
  org.opencontainers.image.authors="Nedeco"

COPY ./build/libs/fama-*.jar /fama.jar

ENTRYPOINT ["java", "-jar", "/fama.jar"]
