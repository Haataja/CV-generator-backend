# CV Generator Backend

## Required tools for running back-end

Ensure you have Java 8 installed.

[Download Maven](https://maven.apache.org/download.cgi)

## Creating Google credentials
Backend needs credentials for the Google project.  
[Instructions for credentials](https://docs.google.com/document/d/1ukXpY2b1lT5D7XCPXAOlG4Nu9u7_mQr6Hjv0W6VSBYk/edit?usp=sharing)

## Running back-end with front-end

- [Build front-end files](https://github.com/Jindetta/CV-generator-frontend/tree/dev)
- Copy build files from *build* directory
- Paste files under *src/main/resources/static*

You can now run back-end with following command:

`mvn spring-boot:run`

### Cors configuration
If 
 - Google login (Security configuration) is not in use
 - Google data storage is not in use 
 - and you are developing frontend from development environment (localhost:3000)  

you must allow connection to backend from frontend http development server.
To do this uncomment `@Configuration` from CORSConfiguration class.