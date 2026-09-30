---
title: API integration
date: '2026-09-29T17:42:00+02:00'
draft: false
description: MyGarage - API integration and vehicle data processing
summary: Integration with MotorAPI, JSON, DTOs, and conversion to entities
categories: {Project Log}
series: MyGarage
series_order: 5
---------------

## API Integration in MyGarage

This week I continued working on MyGarage by integrating an external API that allows the application to retrieve vehicle information using a registration number. The goal is to make it possible to enter a registration number and automatically retrieve information about the vehicle instead of requiring the user to enter all the information manually.

I am using MotorAPI to retrieve vehicle data. The API works as a traditional REST API, where I send an HTTP GET request to an endpoint containing the vehicle's registration number.

My Java implementation uses the `HttpClient` from `java.net.http`:


HttpRequest httpRequest = HttpRequest.newBuilder()
        .uri(new URI(baseUrl + registrationNumber))
        .header("X-AUTH-TOKEN", apiKey)
        .GET()
        .build();

HttpResponse<String> httpResponse =
        httpClient.send(
                httpRequest,
                HttpResponse.BodyHandlers.ofString()
        );


The API key is retrieved from an environment variable instead of being written directly into the source code:


String apiKey = System.getenv("VEHICLE_API_KEY");


This means that the secret API key does not have to be stored in the project's source code.

After sending the request, I check the HTTP status code. If the API does not return `200 OK`, the method throws an exception:


if (httpResponse.statusCode() != 200) {
    throw new RuntimeException("Failed: " + httpResponse.statusCode());
}


If the request succeeds, the method returns the response body as a JSON string.

## Converting JSON into Java Objects

The response from the API is returned as JSON. JSON is a text-based format that is useful for transferring structured data between different systems. A vehicle can contain information about its make, model, engine, registration number, VIN, inspection information, and other properties.

Instead of working directly with the JSON string throughout the application, I use DTOs.

I use Jackson's `ObjectMapper` to convert the JSON response into a `VehicleDTO`:


public VehicleDTO convertFromJson(String json) {
    try {
        return objectMapper.readValue(json, VehicleDTO.class);
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
}


This allows the external data structure to be represented as normal Java objects. I can then work with the data using methods such as:


vehicleDTO.getMake();
vehicleDTO.getModel();
vehicleDTO.getVin();
vehicleDTO.getEnginePower();


instead of manually extracting values from the JSON string.

The DTO therefore acts as an intermediate representation between the format used by the external API and the objects used internally by the application.

## Why I Do Not Persist the DTO

I decided not to use `VehicleDTO` as my database entity. The DTO represents the data structure received from the external API, while the `Car` entity represents how vehicle data is stored and used inside MyGarage.

To keep these responsibilities separate, I created a `ConvertToEntity` service that converts a `VehicleDTO` into a `Car` entity.


public Car convertToCarEntity(VehicleDTO vehicleDTO) {

    Car car = new Car();

    car.setMake(vehicleDTO.getMake());
    car.setModel(vehicleDTO.getModel());
    car.setVariant(vehicleDTO.getVariant());
    car.setYear(vehicleDTO.getModelYear());

    car.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
    car.setVin(vehicleDTO.getVin());

    car.setFuelType(vehicleDTO.getFuelType());
    car.setEngineVolume(vehicleDTO.getEngineVolume());
    car.setEnginePower(vehicleDTO.getEnginePower());

    car.setDoors(vehicleDTO.getDoors());
    car.setSeats(vehicleDTO.getSeats());
    car.setTotalWeight(vehicleDTO.getTotalWeight());

    // ...

    return car;
}


This creates a clear separation between external data and internal application data.

The overall flow is:


MotorAPI
   ↓
JSON
   ↓
VehicleDTO
   ↓
ConvertToEntity
   ↓
Car
   ↓
Database


This separation is useful because the external API can change its data structure without directly determining how my database entity is structured. I also have control over which information from the API is actually stored in MyGarage.

## Processing Inspection Information

Some of the vehicle information is located deeper in the JSON structure. For example, inspection information is contained inside `motInfo`.

I therefore check whether this information exists before trying to use it:


if (vehicleDTO.getMotInfo() != null) {
    car.setMileage(vehicleDTO.getMotInfo().getMileage());

    car.setLastInspectionDate(
            LocalDate.parse(vehicleDTO.getMotInfo().getDate())
    );

    car.setInspectionResult(
            vehicleDTO.getMotInfo().getResult()
    );

    car.setNextInspectionDate(
            LocalDate.parse(
                    vehicleDTO.getMotInfo().getNextInspectionDate()
            )
    );
}


The data is also converted from the date format returned by the API into Java's `LocalDate`. This means that the `Car` entity does not have to store dates as arbitrary strings.

This also demonstrates why DTOs are useful when working with more complex JSON structures. `VehicleDTO` can contain other DTOs, such as a DTO representing inspection information, allowing the structure returned by the API to be represented naturally in Java.

## Testing the Integration

I have tested the integration using a specific registration number:


EC74058


The API returns information about the vehicle, which is then converted from JSON into a `VehicleDTO` and finally into a `Car`.

In my test, I verify that the conversion produces the expected values:

assertEquals("EC74058", car.getRegistrationNumber());
assertEquals("AUDI", car.getMake());
assertEquals("A 4 LIMOUSINE", car.getModel());
assertEquals("2,0 TDI", car.getVariant());
assertEquals("WAUZZZ8E67A243220", car.getVin());

assertEquals("Diesel", car.getFuelType());
assertEquals(1968, car.getEngineVolume());
assertEquals(103, car.getEnginePower());

assertEquals(4, car.getDoors());
assertEquals(5, car.getSeats());
assertEquals(1980, car.getTotalWeight());


This means I am testing more than just whether the API responds. I am also testing whether the data is correctly processed as it moves through the different layers of the application.

## From External Data to MyGarage

The complete flow means that the user does not need to know anything about the structure used by MotorAPI. MyGarage can take a registration number, retrieve the vehicle data, deserialize the JSON into DTOs, and then convert the relevant information into its own `Car` entity.

The data flow can therefore be summarized as:

```text
Registration Number
        ↓
HTTP GET Request
        ↓
MotorAPI
        ↓
JSON Response
        ↓
Jackson ObjectMapper
        ↓
VehicleDTO
        ↓
ConvertToEntity
        ↓
Car Entity
        ↓
JPA / Database
```

One advantage of this structure is that each component has a specific responsibility. `VehicleApiReader` handles communication with the external API, the DTOs represent the received data structure, and `ConvertToEntity` handles the conversion into MyGarage's internal model.

This also makes the application easier to test. If the API changes its JSON structure, the integration and DTO layer can be adapted without necessarily changing the rest of the application, which can continue working with the `Car` entity.


