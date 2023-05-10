FROM openjdk:11

RUN wget -qO - https://artifacts.elastic.co/GPG-KEY-elasticsearch | apt-key add -
RUN apt-get install apt-transport-https
RUN echo "deb https://artifacts.elastic.co/packages/oss-8.x/apt stable main" | tee -a /etc/apt/sources.list.d/elastic-8.7.list
RUN apt-get update && apt-get install metricbeat

COPY ./metricbeat/metricbeat.yml /etc/metricbeat/metricbeat.yml

COPY ./build/libs/song-service.jar app.jar
COPY run.sh run.sh

ENTRYPOINT ["sh", "run.sh"]
