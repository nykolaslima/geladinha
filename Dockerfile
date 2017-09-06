FROM java:8-jre-alpine

ARG version

RUN apk add --update \
    bash \
    && rm     -rf /var/cache/apk/*

COPY ./target/universal/geladinha-${version}.tgz /usr/local/app/geladinha-${version}.tgz

WORKDIR /usr/local/app

RUN tar xf geladinha-${version}.tgz
RUN rm geladinha-${version}.tgz

WORKDIR /usr/local/app/geladinha-${version}

EXPOSE 8080

ENTRYPOINT ["./bin/geladinha"]

