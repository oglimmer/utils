FROM maven:3-jdk-11-slim as build-env

COPY . /opt/build

RUN cd /opt/build && \
	mvn package

