FROM eclipse-temurin:21-jre-noble

RUN useradd --system --home-dir /client --shell /usr/sbin/nologin kobweb \
    && mkdir -p /client/.kobweb/server \
    && chown kobweb /client/.kobweb/server

WORKDIR /client

COPY .kobweb/conf.yaml .kobweb/conf.yaml
COPY .kobweb/server/server.jar .kobweb/server/server.jar
COPY .kobweb/site .kobweb/site

USER kobweb

EXPOSE 8081

ENTRYPOINT ["java", "-Dkobweb.server.environment=PROD", "-Dkobweb.site.layout=FULLSTACK", "-Dio.ktor.development=false", "-jar", ".kobweb/server/server.jar"]
