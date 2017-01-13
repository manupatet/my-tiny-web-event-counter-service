About my-tiny-web-event-counter-service
=========================

This is a Spring Boot application (please add more info here)...

Prerequisites
=============

* Java 1.8
* Maven 3.1.1+

Building
========

From IDE (Eclipse, Intellij)
----------------------------

Open as a Maven project and compile.

From Command Line
-----------------

    mvn clean package

Running the Application
======================

Run or debug the app with the ```Starter``` main class at the root of your Java package hierarchy

You must add the following VM options in order for logging to work properly:

    -Dapplication.home=. -Dproject.name=my-tiny-web-event-counter-service -Dspring.profiles.active=dev -Dapplication.environment=dev

Alternately, you may use either of the following Maven targets to run the application from either the command line or 
your IDE:

    mvn spring-boot:run

or:

    mvn exec:exec

Open a browser and visit [http://localhost:8080/](http://localhost:8080/) for Swagger documentation, or 
[http://localhost:8080/service/hello](http://localhost:8080/service/hello) for the sample API.


Docker
======

Docker Prerequisites
--------------------


Building the Docker Image
-------------------------

```
mvn -DchangeNumber=$(git rev-parse HEAD) -DbuildBranch=origin/master clean package
```

```
docker build -t my-tiny-web-event-counter-service .
```

Running the Docker Image
------------------------

```
docker run -e "APP_NAME=my-tiny-web-event-counter-service" -e "YOUR_ENVIRONMENT=dev" -e "ACTIVE_VERSION=$(git rev-parse HEAD)" -p 8080:8080 my-tiny-web-event-counter-service
```

Open a browser and visit [http://LOCAL_DOCKER_IP:8080/](http://LOCAL_DOCKER_IP:8080/) (use, for example, 
```docker-machine ip default``` to get the ip address).


Spring Boot Configuration Properties
====================================

Please see [here](http://docs.spring.io/spring-boot/docs/current/reference/html/common-application-properties.html) 
for details.
