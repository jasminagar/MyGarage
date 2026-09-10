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
        app.get("/cars/{userId}", ctx -> getCarByUserId(ctx));
        app.post("/cars/create", ctx -> createCar(ctx));
        app.put("/cars/update/{carId}", ctx -> updateCar(ctx));
        app.delete("/cars/delete", ctx -> deletecar(ctx));
    }

    private void getAllCars(Context ctx) {
        List<Car> allCars = carDao.findAllcars();
        ctx.json(allCars);
    }

    private void getCarById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("carId"));
        Car car = carDao.findCarById(id);

        if (car == null) {
            context.status(404);
            context.json("Car not found");
            return;
        }

        context.json(car);
    }

    private void getCarByUserId(Context context){
        Integer userId = Integer.parseInt(context.pathParam("userId"));

        if (userId == null){
            context.status(404);
            context.json("User not found");
        }
        List<Car> cars = carDao.findCarByUserId(userId);

        if (cars.isEmpty()){
            context.json("You have no cars yet");
        }
        context.json(cars);
    }

    private void createCar(Context context){
        Car car = context.bodyAsClass(Car.class);
        Car createdCar = carDao.createCar(car);
        context.status(201);
        context.json(createdCar);
    }

    private void updateCar(Context context){
        Integer carId = Integer.parseInt(context.pathParam("carId"));
        Car car = context.bodyAsClass(Car.class);
        car.setId(carId);
        Car updatedCar = carDao.updateCar(car);
        context.json(updatedCar);
    }

    private void deletecar(Context context){
        Integer carId = Integer.parseInt(context.pathParam("carId"));
        Car car = carDao.findCarById(carId);

        if(car == null){
            context.status(404);
            context.json("Car not found");
        }

        carDao.deleteCar(car);
        context.status(204);
    }

}
