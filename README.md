# The Order Book
The Order Book is a simple application that allows users to create orders, retrieve orders by id and retrieve a summary of orders by ticker and date.

## Create the database
Intellij users:
- Navigate to Services
- Connect to Docker (if you are not connected already)
- Navigate to `docker-compose.yml`
- Click on the run icon on line one

Terminal users:    
`docker-compose up -d`

## How to start the application
Intellij users:    
Run `mvn clean install`    
Use the 'OrderBookApplication' run configuration to start the application

Terminal users:    
`mvn clean install`    
`mvn spring-boot:run`

You can find swagger here - [Swagger UI](http://localhost:8080/swagger-ui.html)

## Description
The application has the following endpoints:
* Post order  
* Get order  
* Get order summary

## Tech stack
- Java 23
- SpringBoot 3.3.0
- MySQL 8.0.4
- Flyway 9.8.1
- Maven
- Docker