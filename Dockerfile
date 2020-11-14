FROM adoptopenjdk/openjdk11:alpine-jre
WORKDIR /opt/app
COPY $TRAVIS_BUILD_DIR/target/driver-pool-1.0-SNAPSHOT.jar ./driver-pool.jar
ENTRYPOINT ["java","-jar","driver-pool.jar"]