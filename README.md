# Bonnie

A training application for newcomers.

## Architecture

This application implements the hexagonal architecture which is a modular application design pattern.

### Modules (a.k.a Plugins)

### businessrules

The "center" of the hexagonal architecture, contains all the business logic, defines interfaces for data storage, and
messaging. Every other module depend on this core module (it is not a plugin).

### authentication

Using Spring Security the authentication module provides a secure channel to handle requests.

### rest

RESTFUL API for the frontend, calls the core module.

### postgres-storage

A data storage plugin that uses Spring JDBC connection to Postgres.
The Postgres database runs separately, typically on the VPS (virtual private server) in a docker container. Other options are available too.

### messaging

Integration module with Kafka. The system gets and send asynchronous events through it.

### starter

It packs all the modules into a single spring boot application. It is a build with a specific set of plugins.
The idea is that, we can have multiple starter modules, with other plugin configurations (with another database plugin for instance).
You have to start this module, if you want to run the application

## Stack

### Spring

This project uses Spring Boot framework.
We use it for dependency injection and configuration management, but also because it has battle-tested integration
packages with all of the major cloud native components.

### Postgres database

For persistence storage, the project includes a Postgres database running on the VPS (Virtual Private Server) within a docker container.
To set up and connect to Postgres from your local development environment see the postgres documentation in [docs/postgres.md](docs/postgres.md).

### Kafka

The project uses Kafka as the message broker. You have to install, and configure it separately before starting the
application. For more information how to do that read the corresponding document in [docs/runKafka.md](docs/runKafka.md) the doc folder.

### Facebook integration

Bonnie provides multiple ways of authentication, such as login form and login via Facebook.
As the application is in the development stage, in order to be able to login through Facebook, you need to set up your
environment. For more information how to do that read the corresponding document in [docs/FacebookIntegration-HowTo.md](docs/FacebookIntegration-HowTo.md).

### Angular frontend

This application has an angular frontend to manage the orderings. A web domain has been registered for Bonnie deployment on https://bonnee.eu/

### Jenkins & Docker

To make the process of *development -> deploy* faster, easier and automatic we use a continuous integration tool **Jenkins** and
containerization platform **Docker**. Both Jenkins and Docker have been installed on our remote server
which can be accessed on [https://bonnee.eu/ci/](https://bonnee.eu/ci/) or via SSH CLI.
For more information on Jenkins and Docker setup read the corresponding document in [docs/Jenkins-Docker.md](docs/Jenkins-Docker.md).

# Building & Running the application locally

First, start the zookeeper, and kafka services that is described in [docs/runKafka.md](docs/runKafka.md).
Then set up the postgres database as described in [docs/postgres.md](docs/postgres.md).
To build the application, issue the following command in the parent project folder

```bash
mvn clean install
```

## Running the application

### Running the backend

(If encryption was not used) You can run the bonnie backend with following command from the ./starter directory:

```bash
mvn spring-boot:run
```

(If encryption was used) You can run the bonnie backend with this command from the ./starter directory:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--jasypt.encryptor.password=BONNEE_MASTER_PWD --spring.datasource.password=ENC(6KpVjqrPwKvLt/5Cjo2ZHg==)"
```

In case you are running Postgres locally, this is the command where you can specify the jdbc url:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--jasypt.encryptor.password=BONNEE_MASTER_PWD --spring.datasource.password=ENC(6KpVjqrPwKvLt/5Cjo2ZHg==)" -Dspring.datasource.url=jdbc:postgresql://localhost:anyport/dbname
```

### Running the frontend

To run the angular frontend, issue the following command from the frontend folder:

```bash
ng serve
```

# Bonnie live environment

The Bonnie application is up and running as a docker container on [https://bonnee.eu/](https://bonnee.eu/).
Bonnie has a multibranch build pipeline in Jenkins. This pipeline is triggered by git push events and build new artifacts of Bonnie backend and frontend.
In the last stage of the pipeline we restart Bonnie with the new versions of backend and frontend.
You can see the build pipeline here: https://bonnee.eu/ci/job/build-bonnie/. You find the pipeline definition in the file names "Jenkinsfile" under bonnie's repository.

# FAQ

**Q: What SDK should I use for Bonnie?**
A: OpenJDK v.18 (non-licensed preferably). Intellij IDEA allows to manage different versions of JDKs for each project
so this feature is recommended rather than installing java separately.

**Q: Do I need to Install Jenkins / Docker on my local machine?**
A: There's no need to install Jenkins nor Docker. In fact, it is not allowed to install Docker on the company machines due to licensing.
Docker and Jenkins are both installed on our remote server.

**Q: What accounts can I use to test Bonnie's functions?**
A: You can find the user account information in V0X_XX__insert_assembly_user_data.sql file in order to login. e.g.(postgres-storage/src/main/resources/db/doodle/V01_03__insert_assembly_user_data.sql)

**Q: How can I contribute to the Bonnie repository on Github?**
A: You have to be added on the collaborator list by the repository owner in order to create/merge branches.

**Q: Where can I get tasks I can work on?**
A: There are tasks for Bonnie in the "Issues" section on Github. You can create/claim/update the issues once they are discussed and approved (usually on Bonnie standups).
