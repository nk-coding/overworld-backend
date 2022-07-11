# overworld-backend

# Development
## Getting started
Make sure you have the following installed:

- Java: [JDK 1.17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or higher
- Maven: [Maven 3.6.3](https://maven.apache.org/download.cgi)
- Postgres : [Postgres](https://www.postgresql.org/download/)

Alternatively you can use [Docker](https://www.docker.com/)

First you have to change the spring.datasource.username and the spring.datasource.password in the application.properties file. If you changed the properties of the postgres db, you also have to change spring.datasource.url.


### Run 
```sh
mvn install
```
in the folder of the project.
Go to the target folder and run 
```sh
java -jar crossword-service-0.0.1-SNAPSHOT.jar
```

### With Docker

Build the Docker container with
```sh
docker build  -t overworld-backend-dev .
```
And run it at port 8000 with 
```
docker run -d -p 8000:80 -e POSTGRES_URL="postgresql://host.docker.internal:5432/postgres" -e POSTGRES_USER="postgres" -e POSTGRES_PASSWORD="postgres" --name overworld-backend-dev overworld-backend-dev
```

To monitor, stop and remove the container you can use the following commands:
```sh
docker ps -a -f name=overworld-backend-dev
```
```sh
docker stop overworld-backend-dev
```
```sh
docker rm overworld-backend-dev
```

To run the prebuild container use
```sh
docker run -d -p 8000:80 -e POSTGRES_URL="postgresql://host.docker.internal:5432/postgres" -e POSTGRES_USER="postgres" -e POSTGRES_PASSWORD="postgres" --name overworld-backend ghcr.io/gamify-it/overworld-backend:latest
```


### testing database
to setup a database with docker for testing you can use
```sh
docker run -d -p 5432:5432 -e POSTGRES_USER=postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=postgres  --rm --name overworld-database postgres
```
To stop and remove it simply type
```sh
docker stop overworld-database
```