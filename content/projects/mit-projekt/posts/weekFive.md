---
title: Building the Car REST API
date: '2026-09-29T17:42:00+02:00'
draft: false
description: MyGarage - API integration and vehicle data processing
summary: Integration with MotorAPI, JSON, DTOs, and conversion to entities
categories: {Project Log}
series: MyGarage
series_order: 6
---------------

Building the Car REST API

This week I also continued building the REST API for MyGarage. I created a CarController using Javalin to expose the car functionality through HTTP endpoints.

The controller receives a CarDao through its constructor:

public class CarController {
private final CarDao carDao;

    public CarController(CarDao carDao) {
        this.carDao = carDao;
    }
}

This keeps the controller responsible for handling HTTP requests while the DAO is responsible for communicating with the database.

Defining Routes in Javalin

I created an addRoutes() method where all the car-related endpoints are registered:

public void addRoutes(Javalin app) {
app.get("/cars", ctx -> getAllCars(ctx));
app.get("/cars/{carId}", ctx -> getCarById(ctx));
app.post("/cars/create", ctx -> createCar(ctx));
app.put("/cars/update/{carId}", ctx -> updateCar(ctx));
app.delete("/cars/delete", ctx -> deletecar(ctx));
}

This maps different HTTP methods and URLs to specific controller methods.

For example:

GET    /cars
GET    /cars/{carId}
POST   /cars/create
PUT    /cars/update/{carId}
DELETE /cars/delete

The HTTP method is part of the route. This means that a GET request to /cars and a POST request to /cars could technically be handled by different methods, even though the URI is the same.

The endpoints correspond to the main CRUD operations used by MyGarage.

GET All Cars

The first endpoint retrieves all cars from the database:

private void getAllCars(Context ctx) {
List<Car> allCars = carDao.findAllcars();
ctx.json(allCars);
}

The DAO retrieves the cars, and the controller converts the result into JSON using the Javalin Context.

I tested this endpoint using an IntelliJ HTTP file:

### GET all cars
GET http://localhost:7070/cars
Content-Type: application/json

This allows me to test the endpoint without having to build a frontend first. IntelliJ sends the HTTP request directly to my locally running Javalin application and displays the response.

GET a Car by ID

I also created an endpoint for retrieving a specific car:

private void getCarById(Context context) {

    Integer id = Integer.parseInt(context.pathParam("carId"));

    Car car = carDao.findCarById(id);

    if (car == null) {
        context.status(404);
        context.json("Car not found/null");
        return;
    }

    context.result("Found car " + car.getRegistrationNumber());
}

The route contains a path parameter:

app.get("/cars/{carId}", ctx -> getCarById(ctx));

When I send:

GET http://localhost:7070/cars/2

Javalin extracts 2 from the URL using:

context.pathParam("carId")

I then convert the value from a String to an Integer because the database ID is an integer.

I also added a check for a missing car. If the DAO returns null, the API responds with HTTP 404 Not Found.

This gives the client a meaningful response instead of simply returning an empty result.

Using the Javalin Context

The Context object is used throughout the controller to interact with the current HTTP request and response.

For example, I use it to retrieve path parameters:

Integer carId = Integer.parseInt(context.pathParam("carId"));

I also use it to read request bodies:

Car car = context.bodyAsClass(Car.class);

and to create JSON responses:

context.json(car);

The same object can also be used to set the HTTP status code:

context.status(404);

This makes the Context the main connection between the HTTP request and the Java code handling it.

Creating and Updating Cars

The controller also contains endpoints for creating and updating cars.

When creating a car, I use:

private void createCar(Context context) {
Car car = context.bodyAsClass(Car.class);
Car createdCar = carDao.createCar(car);

    context.status(201);
    context.json(createdCar);
}

Here, the JSON request body is converted directly into a Car object using Javalin's bodyAsClass() method.

After the DAO creates the car, the controller returns the created entity as JSON.

I return status code 201 Created because a new resource has been successfully created.

For updating a car, I use a path parameter to identify which car should be changed:

private void updateCar(Context context) {
Integer carId = Integer.parseInt(context.pathParam("carId"));

    Car car = context.bodyAsClass(Car.class);
    car.setId(carId);

    Car updatedCar = carDao.updateCar(car);

    context.json(updatedCar);
}

The ID comes from the URL while the updated information comes from the request body.

For example, a request could look like:

PUT http://localhost:7070/cars/update/2
Content-Type: application/json

{
"make": "AUDI",
"model": "A 4 LIMOUSINE",
"mileage": 330000
}

The URL identifies the resource while the body contains the data that should be updated.

Deleting Cars

The controller also has a DELETE endpoint:

private void deletecar(Context context) {
Integer carId = Integer.parseInt(context.pathParam("carId"));
Car car = carDao.findCarById(carId);

    if (car == null) {
        context.status(404);
        context.json("Car not found");
    }

    carDao.deleteCar(car);
    context.status(204);
}

The DELETE method first finds the car using its ID. If the car does not exist, the API returns 404 Not Found.

If the car exists, it is deleted and the endpoint returns 204 No Content.

The HTTP methods therefore map naturally to the operations performed by the application:

GET     → Read
POST    → Create
PUT     → Update
DELETE  → Delete
HTTP Status Codes

While working on the endpoints, I have also started using HTTP status codes to communicate the result of a request.

For example, when a car is successfully created, I return:

201 Created

When a requested car does not exist:

404 Not Found

And after successfully deleting a car:

204 No Content

For successful GET requests, Javalin will normally return:

200 OK

These status codes allow the client to understand the result of a request without having to interpret the response body alone.

REST and Resources

The API is structured around resources. In this case, the main resource is a Car.

The resource is identified through the /cars URI:

/cars

A specific car is identified through:

/cars/{carId}

For example:

/cars/2

represents the car with ID 2.

The API uses HTTP methods to describe what should happen to the resource instead of creating a completely different mechanism for every operation.

This gives the API a predictable structure where clients can understand how to interact with cars based on the URI and HTTP method.

Testing with an HTTP File

I have tested two of the endpoints using an IntelliJ HTTP file:

### GET all cars
GET http://localhost:7070/cars
Content-Type: application/json

### GET car by id
GET http://localhost:7070/cars/2
Content-Type: application/json

This has been useful because I can test the REST API independently of a frontend. I can see the actual HTTP status code and response returned by Javalin and verify that the controller and DAO are working together.