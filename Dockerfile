FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app
COPY ../../../../home/travis/build/aliaksandr-budnikau/driver-pool/target/driver-pool-1.0-SNAPSHOT.jar ./driver-pool.jar
ENTRYPOINT ["java","-jar","driver-pool.jar"]