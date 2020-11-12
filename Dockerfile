FROM adoptopenjdk/maven-openjdk11:latest
WORKDIR /opt/app
COPY ./src ./src
COPY ./pom.xml ./pom.xml
RUN mvn clean package
ARG JAR_FILE=target/driver-pool-1.0-SNAPSHOT.jar
RUN cp ${JAR_FILE} ./driver-pool.jar
RUN find . \! -name 'driver-pool.jar' -delete
ENTRYPOINT ["java","-jar","driver-pool.jar"]