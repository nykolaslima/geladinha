# Geladinha

![Build Status](https://circleci.com/gh/nykolaslima/geladinha.svg?&style=shield)

# TL/DR
Start application on development environment
(You must have [Docker](https://www.docker.com/) and [Docker Compose](https://docs.docker.com/compose/) configured in your machine):
```sh
make start/development
```

# How this project was built

The application was created based on [akka-api-template](https://github.com/nykolaslima/akka-api-template) and is 
developed using [Scala](https://www.scala-lang.org/) with [Akka Actors](http://doc.akka.io/docs/akka/current/scala/actors.html).

## Projects

The application is organized in two repositories:
- [geladinha](https://github.com/nykolaslima/geladinha) (API)
- [geladinha-resources](https://github.com/nykolaslima/geladinha-resources) (API contract/interface definition)

### geladinha-resources

`geladinha-resources` was created using [Protobuf](https://developers.google.com/protocol-buffers/) that allow contract 
definition without programming language dependency. It was created in a separated repository in order to make easier 
to release new versions and to consume then on clients. Actually the `geladinha` also consumes `geladinha-resources` 
(see `dependencies/resources` task).
This structure allow clients to generate the code that will represent the `geladinha` interface which speeds up the 
client development and make it easier to update on new releases.
The releases could be seen on [Github releases page](https://github.com/nykolaslima/geladinha-resources/releases) 
and the version changes could easily be seen on [Github](https://github.com/nykolaslima/geladinha-resources/compare/v0.0.3...v0.0.4).

### geladinha

`geladinha` provides a rest API that could be accessed using `JSON` or `Protobuf Binary` payloads. We strongly encourage 
`Protobuf binary` due lower payload size and faster parsing.  

The solution was made using a [Postgres database](https://www.postgresql.org/) with [Postgis](http://postgis.net/) 
extension that provides spatial operations that were used to solve the given problem. 

#### Build, ship and run 

Project utility tasks are made using [Make](https://www.gnu.org/software/make/) in order to organize several tools integrations.

We build and ship the project using [Docker](https://www.docker.com/). We also use [Docker Compose](https://docs.docker.com/compose/)
to manage project dependencies, such as databases.

#### Continuous Integration

[CircleCI](https://circleci.com/) is responsible by run the CI and is configured to run at every Pull Request and on master merges.

#### Tests

- Unit tests - Unit tests are being used to test business rules and orchestration components mocking their dependencies.
- Integration tests - Testing external dependencies integrations, here our Postgres database.
- Acceptance tests - All API endpoints have end-to-end tests that guarantee the expected behaviour.
 
#### Documentation

[Swagger](https://swagger.io/) is used to document API endpoints and their contracts. It also provides an UI to manually 
test the endpoints. This UI could be accessed when application is running [here](http://localhost:8080/api-docs/)

[Protobuf](https://developers.google.com/protocol-buffers/) messages are also documented and they generate useful 
documentation when trying to understand the domain of the API. We have a [Markdown](https://github.com/nykolaslima/geladinha-resources/blob/master/docs/index.markdown)
and [HTML](https://github.com/nykolaslima/geladinha-resources/blob/master/docs/index.html) versions.

#### Logging

All logs are using the [Gelf](http://docs.graylog.org/en/2.3/pages/gelf.html) format which have a well defined pattern 
to support general log messages and also customized ones.


# geladinha specifics

The project was organized in a component based way. Instead of spreading functionalities over "framework"'s logic the 
project favors functionalities as first order structure driver. This means that different layers of a same functionality 
are placed together:

```
tree src/main/scala/com/zxventures/geladinha/components/pointOfSale
├── ActorMessages.scala
├── PointOfSaleRepositoryActor.scala
├── PointOfSaleRepository.scala
├── PointOfSaleRoute.scala
├── PointOfSale.scala
├── PointOfSaleServiceActor.scala
└── PointOfSaleValidator.scala
```

## Tools
  - [Circle CI](https://circleci.com/gh/nykolaslima/geladinha)
  - Swagger: [localhost](http://localhost:8080/api-docs/)

## Requirements

- [Docker](https://docs.docker.com/engine/installation/)
- [Docker Compose](https://docs.docker.com/compose/)

## Makefile

We use a handful `Makefile` that knows how to compile, build, test and publish the application into a docker
image. The whole idea to use `make` aims into the premise of providing almost-zero-setup requirement to run
day-to-day task when developing and deploying an application.

### Tasks

- `make start/development`: Start application on local development environment.
- `make start/clean/development`: Clean started development application dependencies.
- `make build`: Build a self-contained jar with all dependencies included.
- `make image`: Build a Docker image with the latest tag (implicates `build`).
- `make image/publish`: Publishes the built image (implicates `build` and `image`).
- `make dependencies/resources`: Download the [Protobuf files](https://github.com/zxventures/geladinha-resources) and generate the Scala classes that will be used by the project. (Please look at [Makefile](https://github.com/zxventures/geladinha/blob/master/Makefile) in order to configure `proto_version`)
- `make test/compile`: Compile application with test dependencies.
- `make test`: All-in-one command to start requirements, compile and test the application.


## Development

### Running the application

We can run application on development environment through on demand generated application docker image 
using local docker-compose dependencies.

```sh
make start/development
```

In order to clean `start/development` dependencies just run:

```sh
make start/clean/development
```
