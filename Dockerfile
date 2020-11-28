FROM adoptopenjdk/openjdk11:alpine
WORKDIR /opt/app
COPY ./src ./src
COPY ./pom.xml ./pom.xml
COPY mvnw_for_docker ./mvnw
RUN chmod 777 mvnw
COPY ./.mvn ./.mvn
RUN ./mvnw clean install -Dmaven.test.skip=true -DskipTests=true
RUN rm -fr /root/.m2
ARG JAR_FILE=target/driver-pool-1.0-SNAPSHOT.jar
RUN cp ${JAR_FILE} ./driver-pool.jar
RUN find . \! -name 'driver-pool.jar' -delete
ENTRYPOINT ["java","-jar","driver-pool.jar"]