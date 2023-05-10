#!/bin/sh

metricbeat setup --e
service metricbeat start


java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:50005 -jar app.jar
