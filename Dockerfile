#
# Build stage
#
FROM maven:3.8-openjdk-17-slim AS build
COPY pom.xml /home/app/pom.xml
RUN mvn -f /home/app/pom.xml install
COPY src /home/app/src
RUN mvn -f /home/app/pom.xml package -Dmaven.test.skip


#
# Package stage
#
FROM openjdk:17-jdk-slim
COPY --from=build /home/app/target/overworld-service-0.0.1-SNAPSHOT.jar /usr/local/lib/overworld-service.jar
EXPOSE 80
ENV POSTGRES_URL "postgresql://localhost:5432/postgres"
ENV POSTGRES_USER "postgres"
ENV POSTGRES_PASSWORD "postgres"
ENTRYPOINT /usr/local/openjdk-17/bin/java -jar /usr/local/lib/overworld-service.jar --spring.datasource.url=jdbc:${POSTGRES_URL} --server.port=80 --spring.datasource.username=${POSTGRES_USER} --spring.datasource.password=${POSTGRES_PASSWORD}