package controllers;

import dao.CarDao;
import entities.Car;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class CarController {
    private final CarDao carDao;

    public CarController(CarDao carDao) {
        this.carDao = carDao;
    }

    public void addRoutes(Javalin app) {
        app.get("/cars", ctx -> getAllCars(ctx));
        app.get("/cars/{carId}", ctx -> getCarById(ctx));
        //app.get("/cars/{userId}", ctx -> getCarByUserId(ctx));
        app.post("/cars/create", ctx -> createCar(ctx));
        app.put("/cars/update/{carId}", ctx -> updateCar(ctx));
        app.delete("/cars/delete/{carId}", ctx -> deletecar(ctx));
    }

    private void getAllCars(Context ctx) {
        try {
            List<Car> allCars = carDao.findAllcars();
            ctx.status(200);
            ctx.json(allCars);
        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not retrieve cars");
        }
    }

    private void getCarById(Context ctx) {
        try {
            Integer carId = Integer.parseInt(ctx.pathParam("carId"));
            Car car = carDao.findCarById(carId);

            if (car == null) {
                ctx.status(404);
                ctx.json("Car not found");
                return;
            }

            ctx.status(200);
            ctx.json(car);

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.json("Invalid car ID");

        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not retrieve car");
        }
    }

    private void getCarByUserId(Context ctx) {
        try {
            Integer userId = Integer.parseInt(ctx.pathParam("userId"));
            List<Car> cars = carDao.findCarByUserId(userId);

            if (cars.isEmpty()) {
                ctx.status(404);
                ctx.json("No cars found for this user");
                return;
            }

            ctx.status(200);
            ctx.json(cars);

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.json("Invalid user ID");

        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not retrieve cars for user");
        }
    }

    private void createCar(Context ctx) {
        try {
            Car car = ctx.bodyAsClass(Car.class);
            Car createdCar = carDao.createCar(car);
            ctx.status(201);
            ctx.json(createdCar);
        } catch (io.javalin.http.BadRequestResponse e) {
            ctx.status(400);
            ctx.json("Invalid car data");
        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not create car");
        }
    }

    private void updateCar(Context ctx) {
        try {
            Integer carId = Integer.parseInt(ctx.pathParam("carId"));
            Car existingCar = carDao.findCarById(carId);

            if (existingCar == null) {
                ctx.status(404);
                ctx.json("Car not found");
                return;
            }

            Car car = ctx.bodyAsClass(Car.class);
            car.setId(carId);

            Car updatedCar = carDao.updateCar(car);

            ctx.status(200);
            ctx.json(updatedCar);

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.json("Invalid car ID");

        } catch (io.javalin.http.BadRequestResponse e) {
            ctx.status(400);
            ctx.json("Invalid car data");

        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not update car");
        }
    }

    private void deletecar(Context ctx) {
        try {
            Integer carId = Integer.parseInt(ctx.pathParam("carId"));
            Car car = carDao.findCarById(carId);

            if (car == null) {
                ctx.status(404);
                ctx.json("Car not found");
                return;
            }

            carDao.deleteCar(car);
            ctx.status(204);

        } catch (NumberFormatException e) {
            ctx.status(400);
            ctx.json("Invalid car ID");

        } catch (Exception e) {
            ctx.status(500);
            ctx.json("Could not delete car");
        }
    }

}
