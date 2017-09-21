# Geladinha

![Build Status](https://circleci.com/gh/nykolaslima/geladinha.svg?&style=shield)

This application aim to provide a sample application implementing a REST API with Akka technology.  
The endpoints support JSON and binary Protobuf protocols with Swagger as documentation tool.  


- Tools
  - [Circle CI](https://circleci.com/gh/nykolaslima/geladinha),
  - Swagger: [localhost](http://localhost:9090/api-docs/)

## Requirements

- [Docker](https://docs.docker.com/engine/installation/)

## Makefile

We use a handful `Makefile` that knows how to compile, build, test and publish the application into a docker
image. The whole idea to use `make` aims into the premise of providing almost-zero-setup requirement to run
day-to-day task when developing and deploying an application.

### Tasks

- `make build`: Build a self-contained jar with all dependencies included.
- `make image`: Build a Docker image with the latest tag (implicates `build`).
- `make image/publish`: Publishes the built image (implicates `build` and `image`).
- `make dependencies/resources`: Download the [Protobuf files](https://github.com/zxventures/geladinha-resources) and generate the Scala classes that will be used by the project. (Please look at [Makefile](https://github.com/zxventures/geladinha/blob/master/Makefile) in order to configure `proto_version`)
- `make test/compile`: Compile application with test dependencies.
- `make test`: All-in-one command to start requirements, compile and test the application.


## Development

### Running the application

The docker image defines an entrypoint ready to run and optionally pass custom args:

```sh
docker run \
  -p 9090:9090 \
  -e environment=test \
  zxventures/geladinha:latest \
  -Dakka.loglevel=WARN
```
