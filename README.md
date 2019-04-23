# Prerequisites

## Tools

[Download and install Java 8 SDK](https://www.oracle.com/technetwork/java/javaee/downloads/jdk8-downloads-2133151.html)

[Download and install Maven](https://maven.apache.org/download.cgi)

## Configure project

### Creating Google credentials

Backend needs credentials for the Google project.  
[Instructions for credentials](https://docs.google.com/document/d/1ukXpY2b1lT5D7XCPXAOlG4Nu9u7_mQr6Hjv0W6VSBYk/edit?usp=sharing)

### Enable Google integration (optional)

Define your Google credetials in project root (*application.properties*).

`spring.security.oauth2.client.registration.google.client-id=<id>`
`spring.security.oauth2.client.registration.google.client-secret=<secret>`

This can be done even after building project into JAR package.

# Development configuration

## CORS policy

If 
 - Google login (Security configuration) is not in use
 - Google data storage is not in use 
 - and you are developing frontend from development environment (localhost:3000)

you must allow connection to backend from frontend http development server.
To do this uncomment `@Configuration` from CORSConfiguration class.

## Running development project

- [Build front-end files](https://github.com/Jindetta/CV-generator-frontend/tree/dev)
- Copy build files from *build* directory
- Paste files under *src/main/resources/static*

Run back-end with following command:

`mvn spring-boot:run`

# Creating production build

Project can be built into single JAR package with following command:

`mvn package`
