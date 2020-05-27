FROM openjdk:8
COPY ./target/test-1.0-SNAPSHOT.jar /opt/app/
WORKDIR /opt/app/
EXPOSE 8080
CMD java -jar test-1.0-SNAPSHOT.jar