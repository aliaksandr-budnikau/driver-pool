FROM adoptopenjdk/openjdk11:alpine
WORKDIR /opt/app
COPY ./src ./src
COPY ./pom.xml ./pom.xml
COPY ./mvnw ./mvnw
COPY ./.mvn ./.mvn
RUN ./mvnw clean install
ARG JAR_FILE=target/driver-pool-1.0-SNAPSHOT.jar
RUN cp ${JAR_FILE} ./driver-pool.jar
RUN find . \! -name 'driver-pool.jar' -delete
ENTRYPOINT ["java","-jar","driver-pool.jar"]