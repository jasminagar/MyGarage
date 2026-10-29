# MyGarage — Digital Vehicle Service Book

MyGarage is a Java REST API that allows users to import vehicle information using the vehicle's registration number. By integrating with MotorAPI, the system retrieves vehicle details and inspection report information, so users do not have to enter the information manually. This helps ensure that vehicle records are accurate and based on existing data.
Users can manage their vehicles and keep track of service records, expenses and modifications in one place. The project focuses on simplifying maintaining reliable vehicle information and providing secure access through JWT authentication and role-based authorization.
## Project Links

- **Source code:** [GitHub Repository](https://github.com/jasminagar/MyGarage)
- **Portfolio:** [View Portfolio](https://jasminagar.github.io/MyGarage/)
- **Deployed link:** [Deployed API](http://165.245.241.134:7070/api/v1/)

## Features

- **Vehicle management:** Create, retrieve, update and delete records related to a vehicle..
- **External API integration:** Integrate with MotorAPI to retrieve vehicle data and convert API responses into application data models.
- **Database persistence:** Use Hibernate and JPA to map Java entities to a PostgreSQL database.
- **JWT authentication:** Authenticate users through JSON Web Tokens.
- **Authorization:** Apply role-based access control and ownership checks to protect endpoints and vehicle resources.
- **Partial updates:** Support updating selected fields without requiring a complete replacement of an existing resource.
- **Vehicle import:** Handle imported vehicle data through JSON, DTOs and entity conversion.
- **API testing:** Test endpoint behaviour, security rules and vehicle import functionality.

## Technology Stack

| Technology | Purpose |
|---|---|
| Java 17 | Backend language |
| Javalin 6 | REST API and HTTP routing |
| Maven | Dependency management and build automation |
| Hibernate 6 / JPA | Object-relational mapping and persistence |
| PostgreSQL | Relational database |
| Jackson | JSON serialization and deserialization |
| JWT (Auth0 Java JWT) | Token-based authentication |
| jBCrypt | Password hashing |
| JUnit 6 | Automated testing |
| REST Assured | HTTP API testing |
| Mockito | Mocking dependencies in tests |
| Testcontainers | Database-backed integration testing |
| SLF4J / Logback | Application logging |

## Architecture

MyGarage uses a Java backend with a REST API, persistent data storage and integrations with external services. HTTP requests are handled through Javalin, while Hibernate/JPA manages persistence to PostgreSQL.


                     Client
                       |
                       v
                  Javalin REST API
                       |
                       v
              Authentication & Security
              JWT, roles and ownership
                       |
                       v
                Application Logic
                       |
               +-------+-------+
               |               |
               v               v
         Hibernate / JPA    MotorAPI
               |
               v
          PostgreSQL


The diagram represents the main responsibilities of the system rather than every class or dependency in the implementation.

### Key Design Decisions

**REST API with Javalin**

Javalin provides HTTP routing for the backend. Keeping the API separate from the client makes it possible to test endpoints independently and interact with the system using tools such as Postman or REST Assured.

**Persistence with JPA and Hibernate**

JPA entities represent persistent application data, while Hibernate handles the object-relational mapping. PostgreSQL stores the data between application sessions.

**JWT authentication and authorization**

JWT provides a mechanism for identifying authenticated users across API requests. Authentication alone is not sufficient to protect resources, so endpoint authorization also considers user roles and resource ownership.

**DTOs and external API integration**

MotorAPI integration introduces the need to handle external JSON responses separately from internal database entities. DTOs provide a boundary between incoming data and the application's persistence model, making data conversion and validation easier to manage.

**Partial updates**

Partial updates allow a client to modify selected vehicle fields without resending every field. This requires care when distinguishing omitted fields from explicitly supplied values.

**Automated testing**

The project includes tools for unit testing, API testing and mocking. These are used to verify application functionality, API responses, authentication and vehicle import operations.

## Data Model

The database model is centred around users and their vehicles, with related entities for service records, expenses and modifications. Each user can own multiple cars, and each car can have multiple service records, expenses and modifications.

The model also includes a role-based authorization structure, where users can be assigned roles to control access to protected functionality.

The database uses PostgreSQL with foreign key constraints to define relationships between entities and maintain referential integrity.

The complete database structure and entity relationships are illustrated in the [ER diagram](diagrams/ER-diagram.png).

**Important entities**

### User

Represents a registered user who can log in to the API and manage their own vehicles.

Each user has a unique username and a securely stored password. Users can be assigned roles that determine which operations they are allowed to perform. The relationship between users and vehicles ensures that each car can be associated with its owner.

### Vehicle

Represents a vehicle registered in MyGarage and associated with a specific user.

Each car stores information such as its make, model, year, mileage, registration number and technical specifications. It serves as the central entity for related service records, expenses and modifications.

Users can create, retrieve, update and delete vehicle information, as well as import vehicle data from MotorAPI.

## Security

Security is an important part of the project rather than an additional feature.

The backend addresses several separate concerns:

- **Authentication:** Verify the identity of the requester using JWT.
- **Password storage:** Use password hashing rather than storing plaintext passwords.
- **Role-based access control:** Restrict operations according to the user's role.
- **Ownership checks:** Prevent users from accessing or modifying resources they do not own.
- **Input handling:** Process request data through the application's data models and conversion logic.

A valid token does not automatically grant access to every resource. Authorization must also be enforced at the relevant endpoints.

# API Documentation
The API provides multiple endpoints for CRUD operations and security. The most important endpoints are login, register and vehicle import.
##Authentication
Endpoints that require authentication must be called with a valid JWT token in the Authorization header:
Authorization: Bearer <JWT_TOKEN>
How the returned token is saved:
> {%
client.global.set(“token”, responsebody.token);
%}
The API uses role-based authorization to control access to protected endpoints.
1. Register a User
Creates a new user account.
Request
POST /api/v1/auth/register
Content-Type: application/json
{
  "username": "your_username",
  "password": "your_password"
}
Successful Response — 201 Created
{
  "username": "your_username"
}
Registration does not require authentication.
2. Login
Authenticates a user and returns a JWT token for accessing protected endpoints.
Request
POST /api/v1/auth/login
Content-Type: application/json
{
  "username": "your_username",
  "password": "your_password"
}
Successful Response — 200 OK
{
  "token": "<JWT_TOKEN>",
  "username": "your_username"
}
Save the returned token and include it in the Authorization header when calling protected endpoints.
3. Import a Vehicle
Imports vehicle information using the vehicle's registration number, as a pathParameter. The imported information can include technical specifications and inspection report details, such as the last and next inspection dates.
Request
POST /api/v1/service-records/import/CR48908
Authorization: Bearer <JWT_TOKEN>
Replace CR48908 with the vehicle's registration number and <JWT_TOKEN> with a valid token.
Successful Response — 201 Created
{
  "id": 1,
  "make": "Example Make",
  "model": "Example Model",
  "variant": "Example Variant",
  "year": 2020,
  "mileage": 85000,
  "registrationNumber": "CR48908",
  "vin": "EXAMPLEVIN12345678",
  "fuelType": "Petrol",
  "engineVolume": 1998,
  "enginePower": 150,
  "doors": 5,
  "seats": 5,
  "totalWeight": 2000,
  "lastInspectionDate": [
    2025,
    10,
    28
  ],
  "inspectionResult": "Approved",
  "nextInspectionDate": [
    2027,
    10,
    28
  ]
}
Note: The vehicle values above are illustrative examples, not verified data returned by MotorAPI. The date format follows the response structure used by the application.
The endpoint requires authentication and is accessible to users with the USER or ADMIN role.
HTTP Status Codes
The table below outlines the HTTP status codes used to describe successful requests and common errors when interacting with the API. 
Status Code
Meaning
200 OK
The request completed successfully.
201 Created
A user or vehicle resource was created.
400 Bad Request
The request contains invalid data.
401 Unauthorized
Authentication is missing or invalid.
403 Forbidden
The user does not have the required role or permission.

## Testing

The Maven configuration includes tools for testing different parts of the backend.

- **JUnit:** Test application logic and expected behaviour.
- **REST Assured:** Exercise HTTP endpoints and inspect responses.
- **Mockito:** Isolate components by mocking dependencies.
- **Testcontainers:** Run integration tests against a real PostgreSQL instance in a container.

Important test scenarios include successful CRUD operations, invalid requests, authentication failures, unauthorized access, ownership restrictions and vehicle import behaviour.

## Getting Started

### Prerequisites

- Java 17
- Maven
- PostgreSQL
- Git

Docker is useful if you want to run PostgreSQL or integration-test dependencies in containers.

### Clone the Repository

```bash
git clone https://github.com/jasminagar/MyGarage.git
cd MyGarage
```

### Configure the Database

Create a PostgreSQL database and configure the connection settings used by the application.

The database URL, credentials, JWT signing secret and any external API settings must match the application's configuration. Do not commit real credentials or signing secrets to version control.

### Build the Project

```bash
mvn clean package
```

This compiles the project, runs the configured Maven tests and packages the application.

### Run the Application

The Maven configuration builds an executable JAR with `app.Main` as its entry point.

```bash
java -jar target/app.jar
```

Ensure that the database and any required application configuration are available before starting the application.

# Development Notes

## Hibernate Lazy Loading
One challenge during development was handling Hibernate's lazy loading of user roles. The EntityManager was closed before the roles were fetched, causing a LazyInitializationException when the application attempted to access them during authentication and authorization.
I addressed this by ensuring that the roles were loaded while the persistence context was still open. This helped me understand the relationship between Hibernate's persistence context, lazy loading and role-based access control.
## Security and Authorization
Another challenge was ensuring that users could only manage their own vehicles. JWT authentication identified the logged-in user, while role-based authorization controlled access to endpoints for different roles. However, roles alone were not enough, since a user with access to an endpoint should not automatically be able to modify another user's vehicle. I therefore added ownership checks in the service layer to verify that the vehicle belonged to the requesting user before allowing operations on it. This required security to be considered both at the endpoint level, through role checks, and at the service level, through ownership validation.
## Error Handling
Error handling turned out to be more complex than I initially expected. I had to consider different types of errors, including invalid requests, authentication failures and problems occurring during API operations. This taught me the importance of handling errors consistently and returning appropriate HTTP status codes and responses, making the API easier to understand and use.
