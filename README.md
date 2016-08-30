# Couchbase Java SDK with Sync Gateway Example

This example shows how to create a server side application that uses the Couchbase Java SDK for reading and querying data and the Couchbase Sync Gateway RESTful API for writing or removing data.  This strategy makes it easier to create an application that works for both mobile and web.

## The Requirements

There are a few requirements depending on what your end goals are.

* JDK 8+
* Maven
* Node.js 4.0+
* Couchbase Server 4.1+
* Couchbase Sync Gateway 1.3+

This application uses Lambda expressions which require Java 8 or higher.  To build and run this application, Maven is required, although it can be changed to Gradle with some work.  Node.js is required if you wish to make changes to the Angular 2 front-end that is part of this application.  Node.js ships with the Node Package Manager (NPM) which is required when obtaining the Angular 2 CLI.

Couchbase Server is required for hosting the data and Sync Gateway is required for synchronizing writes between the web application and any mobile device that might be connected.

## Running the Application

To run this application, execute the following from the command line after downloading the project:

```sh
/path/to/sync/gateway/bin/sync_gateway sync-gateway-config.json
```

The above command will launch Sync Gateway and connect it to a Couchbase bucket called **fx-example**.  This bucket must exist prior with a primary index created.

With Sync Gateway running, execute the following to run the Java application:

```sh
mvn spring-boot:run
```

This will start a server which can be accessed from http://localhost:8080.

## Running the Application using Docker

Run Couchbase Server and Sync Gateway as explained at https://github.com/couchbaselabs/docker-deployment-example/tree/master/docker.

Update `application.properties` with the address of Couchbase Server and Sync Gateway:

```
sed -i '' s/server.hostname=localhost/$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' db)/g
sed -i '' s/gateway.hostname=localhost/$(docker inspect --format='{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}' gateway)/g
```

Build the Docker image: `mvn clean package -Pdocker`

Run the container: `docker-compose up -d`

## Resources

Couchbase - [http://www.couchbase.com](http://www.couchbase.com)
