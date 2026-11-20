FROM eclipse-temurin:21-jre-noble

RUN useradd --system --home-dir /site --shell /usr/sbin/nologin kobweb \
    && mkdir -p /site/.kobweb/server \
    && chown kobweb /site/.kobweb/server

WORKDIR /site

COPY .kobweb/conf.yaml .kobweb/conf.yaml
COPY .kobweb/server/server.jar .kobweb/server/server.jar
COPY .kobweb/site .kobweb/site

USER kobweb

EXPOSE 8080

ENTRYPOINT ["java", "-Dkobweb.server.environment=PROD", "-Dkobweb.site.layout=FULLSTACK", "-Dio.ktor.development=false", "-jar", ".kobweb/server/server.jar"]
