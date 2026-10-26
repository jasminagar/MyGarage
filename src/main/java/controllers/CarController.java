package controllers;

import dao.CarDao;
import dao.UserDao;
import dto.CarUpdateDTO;
import dto.UserDTO;
import entities.Car;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import security.EnumRoles;
import services.CarSecurityService;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.*;

public class CarController {
    private final CarDao carDao;
    private final UserDao userdao;
    private final CarSecurityService service;

    public CarController(CarDao carDao, UserDao userdao, CarSecurityService service) {
        this.carDao = carDao;
        this.userdao = userdao;
        this.service = service;
    }

    public EndpointGroup getRoutes() {
        return () -> {
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

            UserDTO tokenUser = ctx.attribute("user");
            if (tokenUser == null) {
                throw new UnauthorizedResponse("User is not authenticated");
            }

            CarUpdateDTO updateDTO = ctx.bodyAsClass(CarUpdateDTO.class);

            Car updatedCar = service.updateCarForUser(carId, tokenUser.getUsername(), updateDTO);

            if (updatedCar == null) {
                ctx.status(404);
                ctx.json("Car not found");
                return;
            }

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
            UserDTO tokenUser = ctx.attribute("user");

            if (tokenUser == null) {
                throw new UnauthorizedResponse("User is not authenticated");
            }

            boolean deleted = service.deleteCarForUser(carId, tokenUser.getUsername());


            if (!deleted) {
                ctx.status(404).json("Car not found");
                return;
            }

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
