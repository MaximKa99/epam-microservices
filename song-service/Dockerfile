FROM openjdk:11

ENV SPRING_DATASOURCE_URL="jdbc:postgresql://localhost:5432/postgres"
ENV SPRING_DATASOURCE_USERNAME=user
ENV SPRING_DATASOURCE_PASSWORD=password

COPY ./build/libs/song-service.jar app.jar

CMD ["java", "-jar", "app.jar"]