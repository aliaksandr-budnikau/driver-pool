FROM adoptopenjdk/maven-openjdk11:latest
WORKDIR /opt/app
ARG JAR_FILE=target/driver-pool-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} driver-pool.jar
ENTRYPOINT ["java","-jar","driver-pool.jar"]