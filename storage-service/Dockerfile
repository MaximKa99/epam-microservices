FROM openjdk:11

ENV SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5431/postgres"
ENV SPRING_DATASOURCE_USERNAME=user
ENV SPRING_DATASOURCE_PASSWORD=password

COPY ./build/libs/storage-service.jar app.jar

ENTRYPOINT ["java",  "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50005", "-jar", "app.jar"]