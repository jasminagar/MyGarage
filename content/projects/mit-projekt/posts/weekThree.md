---
date: '2026-08-22T16:42:10+02:00'
title: 'Implementing CRUD with JPA and Testing with Postman'
draft: false
description: MyGarage - week one
summary: Digital servicebook
categories: {Project Log}
series: MyGarage
series_order: 4
---

This week, I implemented the basic CRUD operations for the project using JPA and Hibernate. I have kept the implementation relatively simple for now because I want to understand how the different layers work before adding more advanced abstractions.

## Implementing CRUD with JPA

I created DAO classes responsible for communicating with the database. The DAOs use `EntityManager` to perform database operations.

For creating entities, I use JPA's `persist()` method. Since `persist()` changes the database, I explicitly start and commit a transaction using the `EntityTransaction` provided by the `EntityManager`.

For example:

java
em.getTransaction().begin();

em.persist(car);

em.getTransaction().commit();

For updating entities, I use `merge()`. This allows me to update an existing entity and return the managed version of that entity.

java
Car updatedCar = em.merge(car);


I also implemented delete operations using `remove()`. Before removing an entity, I first find it using its ID. This makes it possible to check whether the entity actually exists before trying to delete it.

## Working with Relationships

The entities in the project are connected through relationships. For example, a `Car` belongs to a `User`, while expenses, modifications and service records are connected to a specific car.

I use JPA annotations such as `@ManyToOne` and `@JoinColumn` to represent these relationships in the database.

For example:

java
@ManyToOne
@JoinColumn(name = "user_id", nullable = false)
private User user;


I also use JPQL when I need to retrieve entities based on their relationships. For example, to find all cars belonging to a specific user:

java
SELECT c FROM Car c WHERE c.user.id = :userId


This allows me to work with the Java entity relationships instead of writing SQL directly.

## Using Lombok

I use Lombok to reduce some of the boilerplate code in my entity classes. For example, annotations such as `@Getter`, `@Setter`, `@NoArgsConstructor` and `@AllArgsConstructor` generate the common methods and constructors automatically.

This keeps the entity classes easier to read and lets me focus more on the actual structure and relationships between the entities.

## Testing the API with Postman

After implementing the CRUD operations, I tested the API using Postman.

I used different HTTP methods to test the endpoints:

* `POST` for creating new entities
* `GET` for retrieving entities
* `PUT` for updating existing entities
* `DELETE` for deleting entities

For example, I tested creating a user with:

http
POST /users/create


and then retrieved the user using:

http
GET /users/1


I also tested updating entities by providing the ID in the URL and sending the updated data as JSON in the request body.

Testing the API with Postman made it easier to verify that the controllers, DAOs and database were working together correctly. It also helped me find issues with things such as incorrect route parameters and entity relationships.

## Why I kept the implementation simple

For now, I have deliberately avoided adding Spring or unnecessary abstraction layers. The purpose is to get a better understanding of how JPA, Hibernate, controllers and DAOs work together.

The current structure is therefore relatively straightforward:

text
Client / Postman
       ↓
   Controller
       ↓
      DAO
       ↓
 EntityManager
       ↓
   PostgreSQL

This makes the flow of a request easier for me to understand. Later, I can add more structure if the project becomes more complex and requires it.

## Reflection

This week gave me a better understanding of how CRUD operations work with JPA and how the different parts of the backend communicate with each other.

I also learned that working with entity relationships requires some extra consideration. It is not enough to simply provide an ID in a JSON request; the related entity needs to be handled correctly by JPA.

My next step is to continue improving the API and work further with the relationships between the different entities.
