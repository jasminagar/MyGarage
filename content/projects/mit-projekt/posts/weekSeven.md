---
title: Security and API Testing
date: '2026-10-16T17:42:00+02:00'
draft: false
description: MyGarage - JWT authentication, authorization, and API testing
summary: Implementing JWT app.security, organizing the Javalin configuration, and testing vehicle import functionality.
layout: portfolio
categories:
  "Project Log"
series: ["MyGarage"]
series_order: 9
---

# Security and API Testing

This week, I focused on securing the MyGarage backend with JWT, organizing the server configuration, and testing the vehicle import functionality. I also worked on understanding how requests move through the application, from authentication and authorization to the controller and service layers.

## JWT Authentication and Authorization

I implemented JWT (JSON Web Token) authentication to identify users when they access the API. After logging in with a username and password, a user receives a token containing information about their identity and roles.

The following method creates the token:

```java
public String createToken(
        UserDTO userDTO,
        String issuer,
        String expTime,
        String secret
) {
    long expirationMillis = Long.parseLong(expTime);

    if (expirationMillis <= 0 || issuer == null || issuer.isBlank()) {
        throw new IllegalArgumentException(
                "Issuer and positive expiration are required"
        );
    }

    Instant now = Instant.now();

    String[] roles = userDTO.getRoles().stream()
            .map(Enum::name)
            .toArray(String[]::new);

    return JWT.create()
            .withIssuer(issuer)
            .withSubject(userDTO.getUsername())
            .withArrayClaim("roles", roles)
            .withIssuedAt(now)
            .withExpiresAt(now.plusMillis(expirationMillis))
            .sign(signingAlgorithm(secret));
}
```

The method first converts the expiration time into milliseconds and checks that the expiration value is positive and the issuer is not blank. It then collects the user's roles into an array of strings.

The JWT contains several claims: the issuer identifies the application that created the token, the subject identifies the user, and the roles claim describes the user's permissions. The issued-at and expiration timestamps record when the token was created and when it stops being valid. Finally, the token is signed using the configured signing algorithm and secret.

This gives the backend a way to identify the user across multiple requests without requiring the user to log in again for every endpoint.

### Authentication and authorization middleware

When a user accesses a protected endpoint, the token is sent in the `Authorization` header using the Bearer scheme. For example, the vehicle import request looks like this:

```http
POST http://localhost:7070/api/v1/service-records/import/CR48908
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json
```

The backend must verify the token before trusting the information it contains. I separated this process into authentication and authorization.

The authentication method extracts the required roles from the matched route, checks whether the endpoint is public, and verifies the token for protected endpoints:

```java
public void authenticate(Context ctx) {
    if (ctx.method().toString().equals("OPTIONS")) {
        ctx.status(200);
        return;
    }

    Set<String> allowedRoles = ctx.routeRoles().stream()
            .map(role -> role.toString().toUpperCase())
            .collect(Collectors.toSet());

    if (isOpenEndpoint(allowedRoles)) {
        return;
    }

    String token = getToken(ctx);
    UserDTO tokenUser = verifyToken(token);
    ctx.attribute("user", tokenUser);
}
```

If the endpoint is public, authentication is skipped. Otherwise, the method retrieves the token, verifies it, and creates a `UserDTO` from the token's information. The resulting user is stored in the Javalin context so that the next middleware and the endpoint can access it.

Authorization then checks whether the authenticated user has a role that permits access:

```java
public void authorize(Context ctx) {
    Set<String> allowedRoles = ctx.routeRoles()
            .stream()
            .map(role -> role.toString().toUpperCase())
            .collect(Collectors.toSet());

    if (isOpenEndpoint(allowedRoles)) {
        return;
    }

    UserDTO user = ctx.attribute("user");

    if (user == null) {
        throw new UnauthorizedResponse(
                "No user was added from the token"
        );
    }

    if (!hasAllowedRole(user, allowedRoles)) {
        throw new ForbiddenResponse(
                "User was not authorized with roles: " + user.getRoles()
                        + ". Needed roles are: " + allowedRoles
        );
    }
}
```

These two methods have different responsibilities. Authentication establishes the user's identity, while authorization checks what that user is allowed to do. If no authenticated user is available, the request is rejected with an unauthorized response. If the user is authenticated but lacks the required role, the backend returns a forbidden response.

### Public registration and login endpoints

Users need to be able to create an account and log in before they have a token. I therefore added separate app.security app.routes:

```java
public EndpointGroup getSecurityRoutes() {
    return () -> {
        post("/auth/register", securityController::register);
        post("/auth/login", securityController::login);
    };
}
```

These endpoints are registered separately from the protected functionality. Registration allows a new user to create an account, while login checks the supplied credentials and returns a JWT. The public-route handling in the app.security middleware allows these requests to proceed without an existing token.

## Centralizing the Javalin Configuration

I organized the server setup in `ApplicationConfig`, where the application's DAOs, app.services, app.controllers, app.security components, and app.routes are created before the server starts.

For example, the vehicle import service is initialized with its dependencies through its constructor:

_______________________
ImportVehicleService importVehicleService =
        new ImportVehicleService(
                vehicleApiReader,
                converter,
                carDao,
                serviceRecordDao
        );
__________________

The service receives a component for retrieving vehicle data from the external API, a converter for transforming the data into app.entities, and DAOs for persisting the car and its service record.

Passing these dependencies into the constructor makes the service's requirements explicit. It also makes testing easier because the real dependencies can be replaced with mocks, allowing the service logic to be tested independently.

The `Routes` class brings the application's endpoints together. Its `getRoutes()` method registers the app.security app.routes and the app.routes belonging to the different app.controllers:

______________________
public EndpointGroup getRoutes() {
    return () -> {
        securityRoutes.getSecurityRoutes().addEndpoints();
        carController.addRoutes();
        userController.addRoutes();
        serviceRecordController.addRoutes();
        modificationController.addRoutes();
        expenseController.addRoutes();

        post(
                "/service-records/import/{registrationNumber}",
                ctx -> carImportController.importVehicle(ctx),
                EnumRoles.USER
        );
    };
}
____________________-

The vehicle import endpoint is assigned the `USER` role. This makes the access requirement part of the route definition rather than leaving it implicit in the controller.

Finally, the app.security methods are registered as middleware when the Javalin application starts:

__________________
app.beforeMatched(securityController::authenticate);
app.beforeMatched(securityController::authorize);
__________________

The middleware runs before the matched endpoint handler. Authentication runs first, allowing the verified user to be added to the context. Authorization then checks the user's permissions before the request reaches the endpoint. This avoids having to repeat the same app.security checks in every controller.

## Testing the Vehicle Import Service

I wrote a unit test for `ImportVehicleService` using JUnit, Mockito, and Hamcrest. The test checks that the service can process vehicle data and create the expected car and service record.

In the test setup, I create mocks for the external API reader, converter, and DAOs:

________________
@BeforeEach
void setUp() {
    apiReader = mock(VehicleApiReader.class);
    converter = mock(ConvertToEntity.class);
    carDao = mock(CarDao.class);
    serviceRecordDao = mock(ServiceRecordDao.class);

    vehicleImportService = new ImportVehicleService(
            apiReader,
            converter,
            carDao,
            serviceRecordDao
    );
}
__________________

The `@BeforeEach` method creates a fresh test setup before each test. Mockito mocks replace the actual dependencies, so the test does not need to call the external vehicle API or write to a real database.

Inside the test, I define the responses that the service should receive:


when(apiReader.getVehicle(registrationNumber)).thenReturn(json);
when(apiReader.convertFromJson(json)).thenReturn(vehicleDTO);
when(converter.convertToCarEntity(vehicleDTO)).thenReturn(car);
when(carDao.createCar(car)).thenReturn(savedCar);
when(converter.convertToServiceRecord(vehicleDTO, savedCar))
        .thenReturn(serviceRecord);


These statements configure the mocks to return predictable objects when specific methods are called. This allows the test to focus on the import workflow rather than the behaviour of external systems.

The service is then called with the registration number and user. I use Hamcrest assertions to check the returned car's properties:

_____________________
Car result = vehicleImportService.importVehicle(
        registrationNumber,
        user
);

assertThat(result, is(notNullValue()));
assertThat(result.getId(), is(2));
assertThat(result.getRegistrationNumber(), is("EC74058"));
assertThat(result.getUser(), is(user));
__________________

These assertions check that the service returns a car, that it has the expected ID and registration number, and that it is associated with the correct user.

I also use Mockito's `verify()` method to confirm that the expected dependencies were called, including saving the car and creating its service record. Together, the assertions and verifications help confirm both the result of the operation and the sequence of important interactions.

## Testing the API Through HTTP Requests

In addition to the unit test, I used IntelliJ's `.http` files to send requests to the running backend.

The first request sends the username and password to the login endpoint. The response contains a JWT, which can be stored in an IntelliJ HTTP Client variable:

```http
> {% 
    client.global.set("token", response.body.token);
%}
```

The token can then be reused in a request to the protected vehicle import endpoint:

```http
POST http://localhost:7070/api/v1/service-records/import/CR48908
Authorization: Bearer {{token}}
Content-Type: application/json
```

This lets me test the API through actual HTTP requests rather than calling the Java methods directly. The login request tests the authentication flow, while the import request checks whether the token is accepted and whether the user has the required role.

Running the application in debug mode also allows me to follow the request through the authentication middleware, authorization middleware, controller, and service. This makes it easier to understand how the different layers work together and to identify where a problem occurs.

The unit test and the HTTP requests serve different purposes. The unit test isolates the import service and checks its logic, while the HTTP requests exercise the integration between the app.security configuration, routing, and application functionality.

## Reflection

This week's work brought together several parts of the backend that need to work correctly as a whole. The JWT implementation provides a way to identify users across requests, and the role checks restrict access to endpoints such as vehicle import. Centralizing the application setup also makes the relationships between the DAOs, app.services, app.controllers, and app.routes easier to follow.

The tests gave me two different ways to check the application: mocks let me test the import service in isolation, while HTTP requests let me observe how the running API handles authentication and protected operations.

Next, I want to expand the API tests to cover invalid and expired tokens, users without the required roles, and error responses. I also want to verify that the authentication and authorization middleware consistently reject requests that should not be allowed.