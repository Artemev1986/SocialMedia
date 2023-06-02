## Social Media API
This is a RESTful API for a social media platform that allows users to register, log in, create posts, communicate with 
other users, subscribe to other users, and receive their activity feed.

Key Features:

User authentication and authorization using secure token-based authentication.
Post management, including creating, updating, and deleting posts.
User interaction through friend requests and messaging.
Subscriptions and activity feed to display the latest posts from subscribed users.
Error handling with clear error messages and input validation.
Well-documented API with descriptions of endpoints and authentication requirements.

Backend Technology
The application is built using Java 17 and Spring Boot, along with several other technologies:

- **Java 17**
- **Maven**
- **Spring Boot (Data, Security)**
- **Hibernate**
- **Postgresql**
- **JWT**
- **Mockito**
- **JUnit**
- **Lombok**
- **Swagger(OpenAPI 3.0)**


### How to run this project :

#### Firstly one need to install PostgreSQL and create "social-media" database in it.
#### User for DB: postgres
#### Password for DB: 817b62
#### It is also necessary to create a "social-media-test" database for integration testing to work correctly.
#### User for DB: postgres
#### Password for DB: 817b62

#### After that one need to build the project by Maven
```sh
##build the project
mvn clean package
```

After that one can run the project with the command:
```sh
##run the project
mvn spring-boot:run
```
Access the API documentation at http://localhost:8080/swagger-ui.html