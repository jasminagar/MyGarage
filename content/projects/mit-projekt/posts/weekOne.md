---
date: '2026-08-22T16:42:10+02:00'
title: 'From the real world to userstories'
draft: false
description: MyGarage - week one
summary: Digital servicebook
categories: {Project Log}
series: MyGarage
series_order: 2
hiddenInHomeList: true
---
When working on a car, there are several things that you need to remember:

-What work has already been done?
-When was the car last serviced?
-Which parts and modifications have been installed?
-How much did a repair or modification cost?
-What is the current mileage?
-What has been spent on the car overall?
-What still needs to be done?

The problem is mainly that the information about cars can become scattered across reciepts, messages, notes and memory.

This made the core idea quite clear. I do not need a complicated automotive platform. I need a simple digital garage where all the important information about a car can be collected and kept over time.

#The Domain Model (The “Map”)

Before starting the implementation, I thought about what information I would personally want to keep track of when owning and working on a car.

The main idea is to have one place where all relevant information about a car can be collected. The domain model is not a database schema yet, but a simple map of the main entities and how they relate to each other.

The system can then build a history of the car over time, making it easier to understand what has been done to it and how much it has cost to own and modify.

This real-world experience is what I used as the starting point for the domain model and the first user stories for MyGarage.
##Key takeaways from the model:

-The Owner: A User can own multiple Cars.
-The Car: The Car is the central entity and has its own garage page.
-Maintenance history: A Car can have multiple Service Records containing information about maintenance and repairs.
-The Build: A Car can have multiple Modifications, making it possible to keep track of how the car has been changed over time.
-Expenses: A Car can have multiple Expenses related to maintenance, repairs, modifications, parts, or other costs.
-External information: Vehicle information can be retrieved from external APIs using the car's registration number or other vehicle information.

The relationships are intentionally kept simple. A Car belongs to one User, while Service Records, Modifications, and Expenses belong to a Car. At this stage, there is no need for more complex relationships.
![MyGarage domain model](/images/mygarage/domain-model.png)