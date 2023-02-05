FROM openjdk:11

ENV AUDIO_BUCKET="audio"

COPY . /app

WORKDIR /app

RUN ./gradlew clean build

CMD ["java", "-jar", "./build/libs/resource-service.jar"]