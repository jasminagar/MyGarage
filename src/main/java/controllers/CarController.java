package controllers;

import dao.CarDao;
import entities.Car;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import security.EnumRoles;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CarController {
    private final CarDao carDao;

    public CarController(CarDao carDao) {
        this.carDao = carDao;
    }

    public EndpointGroup getRoutes(){
        return ()->{
            get("/cars", ctx -> getAllCars(ctx), EnumRoles.ADMIN);
            get("/cars/{carId}", ctx -> getCarById(ctx), EnumRoles.ADMIN);
            put("/cars/update/{carId}", ctx -> updateCar(ctx), EnumRoles.USER, EnumRoles.ADMIN);
            delete("/cars/delete/{carId}", ctx -> deletecar(ctx), EnumRoles.USER, EnumRoles.ADMIN);
        };
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
