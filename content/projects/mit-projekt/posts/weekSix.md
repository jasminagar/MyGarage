---
title: Security and API testing
date: '2026-09-29T17:42:00+02:00'
draft: false
description: MyGarage - API integration and vehicle data processing
summary: Adding security layer and testing endpoints.
categories: {Project Log}
series: MyGarage
series_order: 7
---------------

## Security and API Testing

This week I worked on the security part of the application by adding registration and login endpoints. I chose to keep the authentication routes separated from the other controllers because authentication is its own part of the application and will be used by the rest of the API.

The routes are defined in a `SecurityRoutes` class:

```java
public void addRoutes(Javalin app) {
    app.post("/auth/register", securityController::register);
    app.post("/auth/login", securityController::login);
}
```

I chose this structure because it keeps the route definitions together instead of mixing authentication routes with routes for cars, expenses and modifications. It makes the application easier to navigate and makes it clear which endpoints belong to the security part of the system. Using `POST` also makes sense because both operations send data to the server and change or process data rather than simply retrieving it.

I also created an `ISecurityController` interface:

```java
public interface ISecurityController {

    void login(Context ctx);

    void register(Context ctx);
    
}
```

The interface gives the security controller a clear contract. The `SecurityRoutes` class does not need to know how login or registration is implemented; it only needs to know that the controller provides these methods. This creates a separation between the routing and the actual security logic. It also makes the code easier to change and test because the route layer can work against the interface rather than being tied directly to one implementation.

The registration endpoint was tested with:

```http
POST http://localhost:7070/auth/register
Content-Type: application/json

{
  "username": "bob6",
  "password": "1234"
}
```

The login endpoint was tested separately:

```http
POST http://localhost:7070/auth/login
Content-Type: application/json

{
  "username": "bob3",
  "password": "1234"
}
```

Having these as separate endpoints makes the flow easier to understand. Registration creates the user, while login verifies the credentials of an existing user. I also use BCrypt for the passwords, which means the actual password is not stored directly in the database. This is important because a database leak should not immediately expose all users' original passwords.

I also continued working with the relationships in the application. A modification belongs to a specific car, so I made the car ID part of the URL:

```http
POST http://localhost:7070/cars/2/modifications
Content-Type: application/json

{
  "name": "Nye bremser",
  "category": "REPAIR",
  "date": "2026-10-07",
  "cost": 2500,
  "description": "Nye for- og bagbremser"
}
```

I chose this instead of sending the entire car object in the request because the modification is the resource being created. The car ID is enough to tell the backend which car it belongs to. The DAO can then find the car in the database and create the relationship. This avoids having to duplicate car information in the request and makes the API easier to use.

The same idea is used for expenses:

```http
POST http://localhost:7070/expenses/2/create
Content-Type: application/json

{
  "date": "2026-10-07",
  "category": "FUEL",
  "amount": 600,
  "description": "Tanket bilen"
}
```

An expense is also connected to a car, but it represents a different type of information. Modifications describe repairs or changes to the car, while expenses describe money spent on the car. Keeping them as separate entities means they can have their own endpoints and database operations instead of putting unrelated information into the `Car` entity.

For automated testing, I used Rest Assured together with JUnit and Hamcrest. I chose Rest Assured because it lets me test the API through actual HTTP requests. This is useful because it tests more than just individual Java methods. It tests whether the route exists, whether the request reaches the correct controller, whether the response has the correct status code and whether the returned JSON contains the expected data.

For example:

```java
@Test
void importVehicle_shouldReturn201() {

    given()
            .pathParam("registrationNumber", "EC74058")
            .when()
            .post("http://localhost:7070/service-records/import/{registrationNumber}")
            .then()
            .statusCode(201)
            .body("registrationNumber", is("EC74058"));
}
```

The test first sends a POST request with Rest Assured. It then checks that the server returns HTTP `201`, which means the resource was successfully created. After that, the Hamcrest `is()` matcher checks the actual value of `registrationNumber` in the JSON response.

Using Hamcrest makes the tests more expressive than simply checking whether a request returned something. I can specify exactly what I expect from the response and later use other matchers when I need to check collections, values or multiple conditions.

This approach also means that when I change something in the backend, I can run the tests and quickly see if an endpoint has stopped behaving as expected. The tests therefore work as a way of verifying the API from a client's perspective rather than only verifying the internal implementation.

Overall, I have tried to keep the different parts separated: routes handle the HTTP endpoints, controllers handle the request logic, DAOs handle database operations, and interfaces define what the controllers and security components should provide. This makes it easier to add more endpoints without putting all of the logic into the same classes.
