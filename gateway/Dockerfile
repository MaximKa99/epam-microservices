FROM openjdk:11

COPY ./build/libs/gateway.jar app.jar

ENTRYPOINT ["java",  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50005", "-jar", "app.jar"]