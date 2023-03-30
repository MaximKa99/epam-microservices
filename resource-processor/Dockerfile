FROM openjdk:11

ENV AWS_ENDPOINT="http://localhost:4566"
ENV SERVICES_SONG="http://localhost:8070"
ENV SERVICES_RESOURCE="http://localhost:8080"

COPY ./build/libs/resource-processor.jar app.jar

ENTRYPOINT ["java",  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50005", "-jar", "app.jar"]