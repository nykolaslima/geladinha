#########
# Tasks #
#########

# Setup and start application on development environment through a on demand generated application docker image using
# docker-compose local dependencies (such as databases).
#
#   make start/development
#
start/development: build image dependencies/services development/run

# Teardown start/development dependencies (such as databases)
#
#   make start/clean/development
#
start/clean/development: dependencies/clean/services

development/run:
	- docker run \
        --link postgres:postgres \
        -p 8080:8080 \
        -e environment=development \
        geladinha:$(version) \
        -Dakka.loglevel=INFO

# Build application (fat jar)
build: dependencies/resources dependencies/swagger-ui
	$(_sbt-cmd) universal:packageZipTarball

# Build docker image
image: build
	- docker build \
	--build-arg version=$(version) \
	-t ${PROJECT_NAME}:$(version) .
	- docker tag ${PROJECT_NAME}:$(version) ${PROJECT_NAME}:$(version)

# Start services and third-party dependencies such as postgres, redis, etc
dependencies/services: dependencies/services/run db/migrate
dependencies/services/run:
	- docker-compose up -d

# Stop services and third-party dependencies
dependencies/clean/services:
	- docker-compose stop && docker-compose rm -vf

# Apply migration placed in `/src/main/resources/db/migrations:/flyway/sql` into specified database via
# args `MIGRATE_DB_USER`, `MIGRATE_DB_PASSWORD` and `MIGRATE_DB_URL`:
#
#   make db/migrate MIGRATE_DB_USER="chucknorris" \
#       MIGRATE_DB_PASSWORD="nowthatyouknowyoumustdie" \
#       MIGRATE_DB_URL="jdbc:postgresql://db.expendables.io:5432/jobs"
#
db/migrate:
	- sleep 2
	- $(_flyway_cmd) migrate

# Compile download proto files from `PROTOS_PATH` and output generated classes into `RESOURCES_PATH`
#
dependencies/resources: dependencies/clean/resources fetch/resources
	- $(_protoc_cmd) scalapbc --proto_path=./$(PROTOS_PATH) \
	      --scala_out=flat_package:./$(RESOURCES_PATH) $(shell find "./$(PROTOS_PATH)" -name "*.proto")

# Clean downloaded proto files directory `PROTOS_PATH` and generated classes directory `RESOURCES_PATH`
#
dependencies/clean/resources:
	- rm -rf $(PROTOS_PATH) $(RESOURCES_PATH)

# Download latest version of `swagger-ui` in order to provided a built application with swagger interface
#
dependencies/swagger-ui: dependencies/clean/swagger-ui
	- mkdir -p src/main/resources/public/swagger
	- git clone \
	        --branch v2.2.8 \
	        --depth 1 https://github.com/swagger-api/swagger-ui.git \
	        tmp/swagger 2> /dev/null
	- mv tmp/swagger/dist/** src/main/resources/public/swagger
	- cp src/main/resources/api-docs/index.html src/main/resources/public/swagger/index.html

dependencies/clean/swagger-ui:
	- rm -rf src/main/resources/public/swagger
	- rm -rf tmp/swagger

# Download proto resources from specified Github repository `PROTO_REPOSITORY` and tag `PROTO_VERSION`.
# The downloaded proto files will be placed into `PROTOS_PATH` and it also created the generated
# classes directory `RESOURCES_PATH`.
#
fetch/resources:
	- mkdir -p $(PROTOS_PATH) $(RESOURCES_PATH)
	- git clone \
	        --branch $(PROTO_VERSION) \
	        --depth 1 git@github.com:$(PROTO_REPOSITORY).git \
	        $(PROTOS_PATH) 2> /dev/null

# Setup, run tests and then tear down
#
#   make test
#
test: dependencies/resources dependencies/services test/run test/coverageReport dependencies/clean/services

# Compile project with test folder included
#
#   make/compile
#
test/compile: dependencies/resources
	$(_sbt-cmd) test:compile

# Run tests
#
#   make test/run
#
test/run:
	$(_sbt-cmd-with-dependencies) coverage test

# Run coverage analysis and reports
#
#   make test/coverage
#
test/coverageReport:
	$(_sbt-cmd) coverageReport

###############
# Definitions #
###############

PROJECT_NAME = geladinha

MIGRATE_DB_USER := postgres
MIGRATE_DB_PASSWORD := postgres
MIGRATE_DB_URL := jdbc:postgresql://postgres/geladinha

PROTO_REPOSITORY = nykolaslima/geladinha-resources
PROTO_VERSION = v0.0.4
PROTOS_PATH = tmp/resources
RESOURCES_PATH = src/main/generated-proto

_flyway_cmd = docker run --rm --net host -v ${PWD}/src/main/resources/db/migrations:/flyway/sql \
      shouldbee/flyway \
      -user="$(MIGRATE_DB_USER)" \
      -password="$(MIGRATE_DB_PASSWORD)" \
      -url="$(MIGRATE_DB_URL)"

_protoc_cmd = \
      docker run \
      -v ${PWD}:/target \
      -w /target \
      --rm brennovich/protobuf-tools:latest

version = $(shell git rev-parse --short HEAD | tr -d "\n")

# Replace `options` with desired value
#
# More details: https://www.gnu.org/software/make/manual/make.html#Substitution-Refs
#
_sbt-cmd = $(_sbt-cmd-base:options=)
_sbt-cmd-with-dependencies = $(_sbt-cmd-base:options=--link postgres:postgres)
_sbt-cmd-base := \
	docker run --rm -it \
		-v $(PWD):/target \
		-v $(HOME)/.ivy2:/root/.ivy2 \
		-v $(HOME)/.m2:/root/.m2 \
		-w /target \
		-e VERSION=$(version) \
		options \
		hseeberger/scala-sbt:latest sbt

