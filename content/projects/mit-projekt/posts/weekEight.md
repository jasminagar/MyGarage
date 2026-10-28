---
title: Securing Endpoints and Testing the API
date: '2026-10-23T15:30:00+02:00'
draft: false
description: MyGarage - Resource-level authorization, JWT app.security, and REST API testing
summary: Strengthening endpoint app.security with ownership checks, role-based access control, partial updates, and API testing.
categories: {Project Log}
series: MyGarage
series_order: 9
---

# Securing Endpoints and Testing the API

Last week, I implemented JWT authentication, authorization middleware, and centralized the app.security configuration in Javalin. I also tested the vehicle import functionality using JUnit, Mockito, Hamcrest, and IntelliJ's HTTP Client.

This week, I continued working on app.security after realizing that checking whether a user has access to an endpoint is not enough to protect the data behind it. A user might have the `USER` role and therefore be allowed to access an endpoint, but that does not mean they should be allowed to modify every car, expense, or modification in the system.

I focused on adding resource-level authorization, making sure the correct roles are assigned to endpoints, and improving the way modifications are updated. I also continued working toward testing the API through HTTP requests and verifying both successful operations and access restrictions.

## Resource-Level Authorization

In the previous implementation, I used JWT authentication to identify the user and role-based authorization to determine whether the user could access an endpoint. The authenticated user is stored in the Javalin context as a `UserDTO`, allowing app.controllers to access the verified username and roles.

However, endpoint authorization and resource authorization solve two different problems.

Endpoint authorization determines whether a user has permission to call an operation. Resource authorization determines whether that user is allowed to perform the operation on a specific resource.

For example, both a regular user and an administrator might be allowed to delete an expense. The regular user should only be able to delete expenses associated with their own cars, while an administrator should be able to manage expenses across all users.

I implemented this distinction in the service layer rather than relying only on the controller or route configuration.

### Checking ownership before deleting an expense

The following method illustrates the approach:

```java
public boolean deleteExpenseForCar(
        Integer expenseId,
        String username,
        boolean isAdmin) {

    Expense expense = expenseDao.findExpenseById(expenseId);

    if (expense == null) {
        return false;
    }

    if (isAdmin) {
        expenseDao.deleteExpense(expenseId);
        return true;
    }

    Integer carId = expense.getCar().getId();

    Car ownedCar = findCarForUser(carId, username);

    if (ownedCar == null) {
        return false;
    }

    expenseDao.deleteExpense(expenseId);
    return true;
}
```

The first step is to retrieve the expense from the database. If it does not exist, the method returns `false`, meaning that there is nothing to delete.

Next, the method checks whether the authenticated user is an administrator. Administrators can proceed without an ownership check. For other users, the method retrieves the ID of the car associated with the expense and checks whether that car belongs to the authenticated user.

I already had a `findCarForUser()` method that looks up the user by username and retrieves a car using both the car ID and the user's ID:

```java
public Car findCarForUser(Integer carId, String username) {
    User user = userDao.findUserByUsername(username);

    if (user == null) {
        return null;
    }

    return carDao.findCarByIdAndUserId(
            carId,
            user.getId()
    );
}
```

This is important because the ownership check is performed using the relationship stored in the database. The client cannot establish ownership simply by sending a particular car ID in the request.

If the car does not belong to the user, the operation is rejected. Otherwise, the expense can be deleted.

Keeping this logic in the service layer also means that the ownership rules are applied independently of how the endpoint is called. The controller does not need to implement the same database checks itself.

## Connecting Authorization to the Controller

The controller retrieves the authenticated user from the Javalin context. This user was added by the JWT authentication middleware described in last week's post.

The delete controller looks like this:

```java
private void deleteExpense(Context context) {
    Integer id = Integer.parseInt(
            context.pathParam("id")
    );

    UserDTO tokenUser = context.attribute("user");

    if (tokenUser == null) {
        throw new UnauthorizedResponse(
                "User is not authenticated"
        );
    }

    boolean isAdmin = tokenUser.getRoles()
            .contains(EnumRoles.ADMIN);

    boolean deleted = securityService.deleteExpenseForCar(
            id,
            tokenUser.getUsername(),
            isAdmin
    );

    if (!deleted) {
        context.status(404).json(
                "Expense not found or access denied"
        );
        return;
    }

    context.status(204);
}
```

The controller performs three main tasks. First, it retrieves the resource ID from the URL. Second, it obtains the authenticated user and determines whether the user has the administrator role. Finally, it delegates the operation to the service and translates the result into an HTTP response.

A successful deletion returns `204 No Content`, because the operation succeeded and no response body is needed. If the resource does not exist or the user cannot access it, the controller returns `404 Not Found`.

Using the same response for a missing resource and an inaccessible resource also avoids explicitly confirming that another user's resource exists.

Authentication failures are handled separately. If the context does not contain an authenticated user, the request is rejected with an unauthorized response.

This builds on the separation introduced last week: the middleware authenticates and authorizes access to the endpoint, while the service checks whether the user is permitted to operate on the particular resource.

## Assigning Roles to Endpoints

I also made sure that the endpoints have explicit role requirements in the route configuration.

For example:


delete(
        "/expenses/delete/{id}",
        this::deleteExpense,
        EnumRoles.USER,
        EnumRoles.ADMIN
);


Both regular users and administrators can call this endpoint, but the service determines which expenses they can delete.

This distinction is important. Assigning the `USER` and `ADMIN` roles to a route does not mean that both roles should have unrestricted access to every resource. Route-level authorization controls access to the operation, while resource-level authorization controls access to the underlying data.

I applied the role configuration across the endpoints so that the access requirements are explicit and handled by the existing authentication and authorization middleware.

This approach also reduces duplicated app.security code in app.controllers. Instead of manually checking roles in every endpoint, the route configuration defines which roles are allowed to call it. Additional ownership checks are only needed when access depends on the particular resource.

## Securing Modification Updates

Another area I worked on was updating modifications associated with a car.

Previously, an update endpoint could receive a complete `Modification` object from the client and pass it directly to the DAO. Although this could update the database, it did not provide sufficient control over which fields the client could change or whether the modification belonged to a car the user was authorized to manage.

I changed the approach so that the controller receives a `ModUpdateDTO` instead of a database entity.

The DTO contains the fields that can be updated:

```java
@Data
public class ModUpdateDTO {
    private String name;
    private String category;
    private LocalDate date;
    private Double cost;
    private String description;
}
```

I use `Double` for `cost` rather than the primitive type `double`. This allows the application to distinguish between a cost explicitly set to zero and a cost that was not included in the request.

### Supporting partial updates

The purpose of the DTO is to allow a client to update only the fields it needs to change.

For example, if a user only wants to change the description, the request body can contain:

```json
{
    "description": "New alloy wheels installed"
}
```

The other fields should remain unchanged.

The service therefore checks each field before applying it to the existing entity:

if (app.dto.getName() != null) {
    modification.setName(app.dto.getName());
}

if (app.dto.getCategory() != null) {
    modification.setCategory(app.dto.getCategory());
}

if (app.dto.getDate() != null) {
    modification.setDate(app.dto.getDate());
}

if (app.dto.getCost() != null) {
    modification.setCost(app.dto.getCost());
}

if (app.dto.getDescription() != null) {
    modification.setDescription(app.dto.getDescription());
}


The service first retrieves the existing modification and verifies that it belongs to the specified car. It then checks whether the authenticated user owns the car, unless the user is an administrator.

Only after these checks does it apply the supplied changes and call the DAO to persist the updated entity.

This is safer than accepting a complete entity from the client. It ensures that the application updates the existing database record rather than treating the incoming request as a replacement for the entity.

It also gives the service a central place to enforce both ownership and update rules.

For this implementation, a `null` value means that the field should not be changed. Consequently, explicitly clearing a field to `null` is not supported by this DTO approach without additional handling.

The controller reads both IDs, deserializes the request body into `ModUpdateDTO`, retrieves the authenticated user from the context, and delegates the operation to the service.

The important point is that the controller does not update the database directly. The service remains responsible for checking whether the operation is allowed, and the DAO remains responsible for persistence.

## Testing the API and Its Security

Last week, I used JUnit, Mockito, and Hamcrest to test the vehicle import service independently of external APIs and the database. I also used IntelliJ's HTTP Client to send requests to the running backend and observe how JWT authentication and route authorization worked.

I want to extend this approach to the new app.security checks by testing not only whether an operation succeeds, but also whether it is correctly rejected when the user does not have access.

For the expense deletion endpoint, useful scenarios include:

- An authenticated user deletes an expense belonging to their own car.
- An authenticated user attempts to delete an expense belonging to another user's car.
- An administrator deletes an expense belonging to any user's car.
- A request references an expense that does not exist.
- A request is sent without a valid JWT.
- An authenticated user without an allowed role attempts to call the endpoint.

The modification update endpoint should be tested in a similar way. In addition to the ownership checks, the tests should verify that sending only one field changes that field without overwriting the other values.

These cases can be tested through actual HTTP requests, which makes it possible to verify the complete request flow through the JWT middleware, authorization middleware, controller, service, and DAO.

### Using Rest Assured for API tests

Rest Assured can be used to automate these HTTP-level tests instead of sending each request manually. It allows requests to be constructed in Java and provides assertions for response status codes, headers, and JSON response bodies.

For example, a basic test of an endpoint can follow this structure:

given()
    .header("Authorization", "Bearer " + token)
.when()
    .get("/api/v1/cars")
.then()
    .statusCode(200);

The test sends a request with a JWT and verifies that the API returns `200 OK`. The token can be obtained through the login endpoint, allowing the test to exercise the authentication flow rather than bypassing it.

For authorization tests, I can send the same request with different tokens representing different users or roles and verify the expected response codes. This makes it possible to test whether the route configuration and the resource-level ownership checks work together correctly.

Hamcrest matchers can also be used to assert properties of the response body, such as a returned car's ID or the description of an updated modification. This provides more precise checks than verifying the status code alone.

These are examples of the test structure I intend to use; they do not represent a completed test suite for every scenario described above.

## Password Security

The login flow is another important part of the overall app.security model. JWT protects access to the API after authentication, but the initial login process must also handle credentials securely.

Passwords should not be stored as plaintext in the database. Instead, the application should store a password hash generated with a password-hashing algorithm such as bcrypt. During login, the application verifies the supplied password against the stored hash rather than comparing plaintext passwords.

Bcrypt is designed to make password guessing more computationally expensive and uses a salt to ensure that identical passwords do not necessarily produce identical stored hashes. This helps protect stored credentials if the database is exposed.

This is separate from JWT signing. Password hashing protects stored user credentials, while the JWT signature allows the backend to verify that a token has not been altered. Neither mechanism replaces the other.

The login endpoint must validate credentials before issuing a token, and protected endpoints must continue to validate the token and the user's permissions. The app.security of the API therefore depends on several layers working together rather than on JWT alone.

## Reflection

This week, I expanded the app.security work from last week's JWT implementation by focusing on what authenticated users are actually allowed to do with the application's data.

The distinction between role-based authorization and resource-level authorization was particularly important. A route can permit both users and administrators, but the service must still check ownership before allowing a regular user to modify or delete a resource.

I also improved the modification update workflow by using a DTO and supporting partial updates. This gives the API more predictable update behaviour and avoids requiring clients to submit every field when changing only one value.

The next step is to automate these scenarios with Rest Assured, including successful requests, invalid or missing tokens, insufficient roles, and attempts to access resources belonging to other users. I also want to verify the password-hashing implementation and ensure that authentication failures and authorization failures produce appropriate HTTP responses.

Together, these changes make the backend more consistent: JWT identifies the user, route authorization determines which operations the user may call, service-level checks enforce ownership, and API tests verify that the rules hold when the application is accessed through HTTP.
