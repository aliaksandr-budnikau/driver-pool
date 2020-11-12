FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app
ARG JAR_FILE=./target/driver-pool-1.0-SNAPSHOT.jar
COPY ${JAR_FILE} ./driver-pool.jar
#RUN useradd spring && adduser spring spring
#USER spring:spring
ENTRYPOINT ["java","-jar","driver-pool.jar"]