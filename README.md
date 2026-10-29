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

Javalin was chosen to provide a lightweight REST API for communication between the client and the backend. A REST-based architecture separates the application's interface from its business logic and data management, allowing the backend to be developed, tested and maintained independently of the client. HTTP endpoints expose specific operations through standard methods such as GET, POST, PUT and DELETE, making the API predictable and easy to integrate with external tools. This separation also simplifies automated testing, since endpoints can be tested independently using tools such as Postman and REST Assured without requiring the client application to run.

**Persistence with JPA and Hibernate**

JPA and Hibernate were selected to separate the application's domain model from the underlying database operations. JPA provides a standard way to define entities and their relationships, while Hibernate implements the object-relational mapping between Java objects and relational database tables. This reduces the need to write repetitive SQL queries and makes it easier to manage relationships between entities. PostgreSQL was chosen for persistent storage because it provides relational data integrity, structured querying and transaction support. Together, these technologies provide a maintainable persistence layer in which application data remains available across restarts, while database constraints and transactions help preserve consistency.

**JWT authentication and authorization**

JWT-based authentication was selected to identify users across separate HTTP requests without requiring the server to maintain a traditional session for every client. After successful authentication, the client receives a token that can be included in subsequent requests. The backend validates the token before allowing access to protected endpoints. However, authentication and authorization serve different purposes: authentication establishes who the user is, while authorization determines what the user is permitted to do. The design therefore considers both user roles and resource ownership when protecting operations. Role-based checks restrict access to functionality according to a user's permissions, while ownership checks ensure that users cannot access or modify resources belonging to other users merely by changing an identifier in a request. This separation provides a clearer security model and reduces the risk of unauthorized data access.

**DTOs and external API integration**

DTOs (Data Transfer Objects) were introduced to separate data exchanged through the API from the entities used internally by the application. This is particularly important when integrating with MotorAPI, because external responses may contain fields, naming conventions or data structures that do not match the application's database model. Instead of coupling persistent entities directly to the external API, the response can be mapped to dedicated DTOs and then converted into the appropriate internal representation. This creates a clear boundary between external data and the application's domain model. It also makes validation, error handling and data transformation easier to manage, while reducing the impact of changes to the external API on the persistence layer. The same principle can be applied to incoming client requests, ensuring that only intended fields can be modified through the API.

**Partial updates**

Partial updates were chosen to allow clients to modify individual vehicle attributes without resending the complete resource. This reduces unnecessary data transfer and makes the API more convenient to use, particularly when only a small number of fields need to change. However, partial updates introduce an important design consideration: the backend must distinguish between a field that was omitted from the request and a field that was explicitly supplied with a value such as null. An omitted field should generally remain unchanged, whereas an explicitly supplied value must be handled according to the field's validation and business rules. The implementation must therefore apply updates selectively, validate the resulting data and prevent protected or immutable fields from being modified unintentionally. This approach provides flexibility for clients while preserving the integrity of the stored vehicle data.

**Automated testing**

Automated testing was included to verify the behaviour of the application at different levels and to reduce the risk of regressions when the implementation changes. Unit tests can verify individual components and business rules in isolation, while API tests can check endpoint behaviour, HTTP status codes, response structures and error handling. Authentication and authorization tests are particularly important because they verify that protected operations reject missing or invalid tokens and prevent users from accessing resources they do not own. Mocking external dependencies, such as MotorAPI, allows vehicle import logic to be tested without relying on network availability or changes in the external service. This makes tests more predictable and repeatable. Combining these testing approaches provides greater confidence that individual components work correctly and that the backend behaves as expected when its components interact.

## Data Model

The database model is centred around users and their vehicles, with separate entities for service records, expenses and modifications. This structure reflects a deliberate design choice to separate different types of information into distinct entities rather than storing all vehicle-related data in a single table. Each user can own multiple cars, and each car can have multiple service records, expenses and modifications. This one-to-many relationship structure reduces data duplication and allows each type of record to be managed independently. It also makes the model easier to extend, for example, if additional types of vehicle-related records are introduced in the future.

The database follows a normalized structure by separating data according to its purpose and relationships. User information is stored independently of vehicle data, while service records, expenses and modifications are associated with the relevant vehicle through foreign keys. This reduces the risk of update, insertion and deletion anomalies, where the same information would otherwise need to be maintained in multiple places. It also improves data consistency by ensuring that each entity has a clearly defined responsibility and that relationships between records are explicitly represented.

Security is addressed through a role-based authorization structure, where users can be assigned roles that determine which protected functionality they are permitted to access. Separating roles from user and vehicle data makes authorization rules easier to manage and extend. However, role-based authorization alone is not sufficient for protecting user-owned resources. Access control must also consider resource ownership, ensuring that authenticated users can only access or modify vehicles and related records they are authorized to manage. These checks should be enforced by the backend rather than relying on restrictions implemented in the client.

PostgreSQL is used to enforce referential integrity through primary keys, foreign keys and appropriate constraints. These mechanisms prevent invalid relationships, such as records referencing vehicles that do not exist, and help maintain consistency when related data is changed or deleted. Combined with application-level authorization and validation, the database constraints provide an additional layer of protection against inconsistent or invalid data.

The complete database structure and entity relationships are illustrated in the [ER diagram](diagrams/ER-diagram.png).

**Important entities**

### User

The User entity represents a registered user who can authenticate with the API and manage their own vehicles. It is a fundamental part of the system because it establishes ownership of data and provides the basis for access control. Each user has a unique username and a securely stored password hash, ensuring that credentials can be verified without storing passwords in plain text.

Users can be assigned roles that determine which operations they are permitted to perform. This separates authentication, which establishes the user's identity, from authorization, which determines their permissions. By connecting users to their vehicles, the system can enforce resource ownership and prevent users from accessing or modifying vehicles belonging to other users. This relationship is important not only for security but also for maintaining a clear separation between users' data.

The User entity therefore acts as the starting point for the ownership structure of the database. It allows the application to associate each vehicle with a specific owner and provides a foundation for enforcing access restrictions consistently across vehicle-related operations.

### Vehicle

The Vehicle entity represents a vehicle registered in MyGarage and serves as the central entity for the application's vehicle-related data. Each vehicle is associated with a specific user, establishing an explicit ownership relationship that allows the backend to determine which user is permitted to manage it.

A vehicle stores information such as its make, model, year, mileage, registration number and technical specifications. Keeping these attributes together in a dedicated entity provides a single, consistent representation of the vehicle that can be referenced by other parts of the application. This avoids duplicating core vehicle information across service records, expenses and modifications, making the data easier to maintain and reducing the risk of inconsistencies.

The vehicle also acts as the parent entity for related records, including service history, expenses and modifications. This relationship allows the application to maintain a complete history of each vehicle while keeping each type of information separate. For example, multiple service records can be associated with the same vehicle without repeatedly storing its make, model or registration number. This supports a normalized database structure and makes it possible to retrieve both individual records and a vehicle's overall history.

Users can create, retrieve, update and delete vehicle information, as well as import vehicle data from MotorAPI. The import functionality reduces the need to enter technical information manually and can improve data consistency when external data is mapped and validated before being stored. Regardless of how a vehicle is created, the backend must verify the user's authorization before allowing access or modifications. This ensures that external data integration and ordinary CRUD operations follow the same ownership and security rules.

Together, the User and Vehicle entities establish the core structure of MyGarage: users own vehicles, and vehicles provide the foundation for organizing all associated maintenance, financial and modification data. This relationship connects the application's main functionality with its data integrity and security requirements.

## Security and Input Validation

Security is integrated into the backend layers to protect user accounts, resources, and data. The API layer uses JWT authentication to verify the identity of users making requests. The service layer enforces authorization by checking user roles and resource ownership before allowing operations. The DAO layer handles database access, while entity relationships associate resources with their owners.

Input validation complements these security measures by ensuring that incoming data meets the application's requirements before it is processed or stored. This includes checking data types, required fields, and values to reduce invalid input, prevent data corruption, and minimize security risks. Request data is converted into DTOs and application models, allowing the backend to process it in a controlled manner.

Together, authentication, authorization, ownership checks, and input validation help prevent unauthorized access and ensure that data is handled safely. A valid JWT alone does not guarantee access to a resource; the backend must also verify that the user has permission to perform the requested operation.

# API Documentation

## CRUD Endpoint Overview

Besides authentication and vehicle import, the MyGarage API provides endpoints for managing vehicles and their associated service records, expenses and modifications. Most of these endpoints follow the standard CRUD pattern (Create, Read, Update and Delete). The table below summarizes the typical operations and their HTTP methods.

| Operation | HTTP method | Purpose |
|---|---|---|
| Create | `POST` | Creates a new resource, such as a vehicle, expense or service record. |
| Read | `GET` | Retrieves a resource or a collection of resources. |
| Update | `PUT` | Updates an existing resource with the supplied data. |
| Partial update | `PATCH` | Updates selected fields of an existing resource, where supported. |
| Delete | `DELETE` | Removes an existing resource. |

These operations are implemented across the relevant resource endpoints. The exact URL structure depends on the resource and its relationships. For example, an expense endpoint can include the vehicle ID to identify the car associated with the expense, as well as the expense ID to identify the specific record.

Most CRUD endpoints require authentication and apply role-based authorization. Resource ownership must also be checked where relevant to ensure that users can only manage data they are authorized to access. This approach provides a consistent structure across the API while allowing each resource to enforce its own validation and business rules.

The following example demonstrates how an existing expense is updated.

## Example: Updating an Expense

The expense endpoints illustrate the general CRUD structure used throughout MyGarage. In this example, an existing expense associated with a specific vehicle is updated using an HTTP `PUT` request.

**Request**

```http
PUT http://localhost:7070/api/v1/expenses/car/60/update/10
Authorization: Bearer {{token}}
Content-Type: application/json
```

**Request body**

```json
{
  "date": "2026-10-07",
  "category": "TEST",
  "amount": 600,
  "description": "TEST"
}
```

The URL contains two identifiers: `60` represents the vehicle ID, while `10` represents the expense ID. Together, they identify the expense that should be updated in the context of its associated vehicle.

The request body contains the values submitted for the expense, including its date, category, amount and description. Since the endpoint uses `PUT`, the client should provide all fields required by the update operation rather than assuming that omitted fields will remain unchanged. The precise handling of omitted fields depends on the implementation.

The endpoint requires a valid JWT and is accessible to users with the `USER` or `ADMIN` role. The backend must also verify that the requesting user is authorized to modify the specified expense. Checking only the role is insufficient if users are restricted to managing their own vehicles and related records.

After processing the request, the API returns an appropriate HTTP status code indicating whether the update succeeded or failed. The exact response body and status code depend on the endpoint implementation.

This example demonstrates the general principles applied to CRUD operations throughout MyGarage: HTTP methods communicate the intended operation, URL parameters identify the target resource, JSON bodies carry the relevant data, and authentication and authorization protect access to application resources.

## Endpoints Overview

The following table describes the main endpoints, their HTTP methods and their purposes.

| Method | Endpoint | Purpose | Authentication |
|---|---|---|---|
| `POST` | `/api/v1/auth/register` | Registers a new user account. | Not required |
| `POST` | `/api/v1/auth/login` | Authenticates a user and returns a JWT. | Not required |
| `POST` | `/api/v1/service-records/import/{registrationNumber}` | Imports vehicle information using its registration number. | Required (`USER` or `ADMIN`) |

## Authentication and Authorization

The API uses JSON Web Tokens (JWT) to authenticate requests to protected endpoints. After a successful login, the client receives a token that must be included in the `Authorization` header of subsequent requests.

The header must follow this format:

```http
Authorization: Bearer <JWT_TOKEN>
```

Replace `<JWT_TOKEN>` with the token returned by the login endpoint. The backend validates the token and checks the user's permissions before processing the request. For protected operations, authorization may depend on both the user's role and ownership of the requested resource.

When using the IntelliJ HTTP Client, the returned token can be stored in a global variable for reuse:

```javascript
> {% 
    client.global.set("token", response.body.token);
%}
```

The variable can then be referenced in subsequent requests:

```http
Authorization: Bearer {{token}}
```

## Request and Response Bodies

Endpoints that accept input data use JSON request bodies with the appropriate `Content-Type: application/json` header. For example, registration and login require a username and password. The registration endpoint creates a user account, while the login endpoint validates the credentials and returns a JWT together with the username.

Successful responses use JSON to return relevant information about the operation. Registration returns the created user's username, while login returns the authentication token and username. The vehicle import endpoint returns the imported vehicle's data, including technical specifications and inspection information when available from the external service.

### 1. Register a User

Creates a new user account.

**Request**

```http
POST /api/v1/auth/register
Content-Type: application/json
```

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

**Successful response — `201 Created`**

```json
{
  "username": "your_username"
}
```

Registration does not require authentication.

### 2. Login

Authenticates a user and returns a JWT for subsequent requests to protected endpoints.

**Request**

```http
POST /api/v1/auth/login
Content-Type: application/json
```

```json
{
  "username": "your_username",
  "password": "your_password"
}
```

**Successful response — `200 OK`**

```json
{
  "token": "<JWT_TOKEN>",
  "username": "your_username"
}
```

The returned token must be included in the `Authorization` header when calling protected endpoints.

### 3. Import a Vehicle

Imports vehicle information using its registration number. The backend retrieves the available information from MotorAPI and processes it for use within MyGarage. Depending on the data returned by the external service, the imported information may include technical specifications, mileage-related information and inspection details.

**Request**

```http
POST /api/v1/service-records/import/CR48908
Authorization: Bearer <JWT_TOKEN>
```

Replace `CR48908` with the vehicle's registration number and `<JWT_TOKEN>` with a valid token.

**Successful response — `201 Created`**

```json
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
```

The response values are illustrative examples and do not represent verified data returned by MotorAPI. The inspection dates use the date structure returned by the application, represented as `[year, month, day]`.

The endpoint requires authentication and is available to users with the `USER` or `ADMIN` role. The backend must also enforce the relevant ownership and authorization rules when creating or associating vehicle data.

## HTTP Status Codes

The API uses standard HTTP status codes to communicate the outcome of a request.

| Status code | Meaning |
|---|---|
| `200 OK` | The request completed successfully. |
| `201 Created` | A resource was created successfully, such as a user or imported vehicle. |
| `400 Bad Request` | The request contains invalid data or does not satisfy validation requirements. |
| `401 Unauthorized` | Authentication is missing, invalid or unsuccessful. |
| `403 Forbidden` | The user is authenticated but lacks the required role or permission. |

These status codes help clients distinguish successful operations from validation errors and security-related failures, allowing them to handle API responses appropriately.

## Testing

The backend uses a combination of testing tools to verify individual components, API endpoints and database interactions. Each tool serves a different purpose, helping ensure that the application behaves correctly both in isolation and as a complete system.

- **JUnit:** Provides the foundation for writing and running tests. It is used to verify business logic and expected behaviour, such as calculating modification costs, validating input and handling different outcomes.
- **Mockito:** Makes it possible to replace dependencies with mock objects. This allows services to be tested independently by controlling the behaviour of DAOs or other dependencies without requiring a real database.
- **REST Assured:** Tests the REST API through HTTP requests. It is used to verify endpoint behaviour, response status codes and returned data, including successful requests and error responses.
- **Testcontainers:** Starts a PostgreSQL instance in a container for integration tests. This allows database operations to be tested against a real PostgreSQL database, helping identify issues that mocks alone would not reveal.

### Test Scenarios

The tests cover database operations, service-layer security and API behaviour. Different testing tools are used depending on which part of the application is being tested.

- **Expense CRUD operations:** `ExpenseDAOTest` verifies that expenses can be created, retrieved by ID, retrieved by car ID and deleted. It also checks that creating an expense for a nonexistent car throws a `NotFoundResponse`. These tests use PostgreSQL through Testcontainers, allowing the DAO to be tested against a real database.

- **Expense service security:** `ExpenseServiceTest` verifies that an expense cannot be deleted if it does not exist, that administrators can delete expenses, and that users can delete expenses belonging to their own cars. It also tests that users are forbidden from deleting expenses associated with another user's car. Mockito is used to mock the DAO and security service, allowing the authorization logic to be tested independently of the database.

- **Vehicle import authentication:** `ImportVehicleTest` verifies that an unauthenticated request to the vehicle import endpoint receives HTTP 401. This checks that authentication is required before the endpoint can be accessed.

- **Successful vehicle import:** `ImportVehicleTest` logs in with test credentials, extracts the JWT from the login response and uses it to call the import endpoint. The test expects HTTP 201, verifying that the endpoint accepts an authenticated request and reports successful creation.

Together, these tests cover database persistence, resource handling, ownership checks and authentication at different levels of the application. The DAO tests focus on database behaviour, the service tests isolate business and security logic, and the REST Assured tests exercise the API through HTTP requests.

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

# User stories

- US-01: Opret bruger
Som ny bruger vil jeg kunne oprette en konto, så jeg kan bruge MyGarage.

- US-02: Log ind
Som bruger vil jeg kunne logge ind, så jeg får adgang til mine egne biler..

- US-03: Administrer biler
Som bruger vil jeg kunne oprette, se, redigere og slette mine biler, så jeg kan holde styr på dem.

- US-04: Importer biloplysninger
Som bruger vil jeg kunne importere oplysninger om en bil via registreringsnummer, så jeg slipper for at indtaste alle oplysninger manuelt.

- US-05: Administrer servicehistorik
Som bruger vil jeg kunne gemme og se synshistorikken for mine biler, så jeg kan holde styr på tidligere og kommende vedligeholdelse.

- US-06: Registrer udgifter og ændringer
Som bruger vil jeg kunne registrere udgifter og ændringer på mine biler, så jeg kan holde styr på bilernes historik og omkostninger.

- US-07: Rediger biloplysninger
Som bruger vil jeg kunne opdatere enkelte oplysninger om mine biler, uden at skulle ændre alle oplysningerne.

- US-08: Beskyt mine oplysninger
Som bruger vil jeg kun kunne se og ændre mine egne biler og tilhørende oplysninger, så andre brugere ikke kan få adgang til eller ændre mine data. 

- US-09: Håndter fejl
Som bruger vil jeg have tydelige fejlbeskeder, når noget går galt, så jeg forstår, hvorfor en handling ikke lykkes.

- US-10: Se bilens samlede udgifter
Som bruger vil jeg kunne se de samlede udgifter for mine biler, så jeg kan få et overblik over, hvor meget jeg bruger på vedligeholdelse og andre bilrelaterede omkostninger. 


# Development Notes

## Hibernate Lazy Loading
One challenge during development was handling Hibernate's lazy loading of user roles. The EntityManager was closed before the roles were fetched, causing a LazyInitializationException when the application attempted to access them during authentication and authorization.
I addressed this by ensuring that the roles were loaded while the persistence context was still open. This helped me understand the relationship between Hibernate's persistence context, lazy loading and role-based access control.
## Security and Authorization
Another challenge was ensuring that users could only manage their own vehicles. JWT authentication identified the logged-in user, while role-based authorization controlled access to endpoints for different roles. However, roles alone were not enough, since a user with access to an endpoint should not automatically be able to modify another user's vehicle. I therefore added ownership checks in the service layer to verify that the vehicle belonged to the requesting user before allowing operations on it. This required security to be considered both at the endpoint level, through role checks, and at the service level, through ownership validation.
## Error Handling
Error handling turned out to be more complex than I initially expected. I had to consider different types of errors, including invalid requests, authentication failures and problems occurring during API operations. This taught me the importance of handling errors consistently and returning appropriate HTTP status codes and responses, making the API easier to understand and use.
