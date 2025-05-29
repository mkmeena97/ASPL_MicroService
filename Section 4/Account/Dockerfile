# Start with a base image containing Java Runtime
FROM openjdk:24-jdk-slim

MAINTAINER mahendrakumar27697@gmail.com

# copy jar file present the target folder to docker image and name should be of that copied jar file "account-0.0.1-SNAPSHOT.jar"
COPY target/Account-0.0.1-SNAPSHOT.jar account-0.0.1-SNAPSHOT.jar

#execute the application using command (java -jar account-0.0.1-SNAPSHOT.jar)
ENTRYPOINT ["java","-jar","account-0.0.1-SNAPSHOT.jar"]