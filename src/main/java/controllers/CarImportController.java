package controllers;

import dao.UserDao;
import dto.UserDTO;
import entities.Car;
import entities.User;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import security.EnumRoles;
import services.ImportVehicleService;

import static io.javalin.apibuilder.ApiBuilder.post;

public class CarImportController {
    private final ImportVehicleService importVehicleService;
    private final UserDao userDao;

    public CarImportController(ImportVehicleService importVehicleService, UserDao userDao) {
        this.importVehicleService = importVehicleService;
        this.userDao = userDao;
    }

    public void importVehicle(Context context) {
        String registrationNumber = context.pathParam("registrationNumber");
        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        User user = userDao.findUserByUsername(tokenUser.getUsername());
        Car importedCar = importVehicleService.importVehicle(registrationNumber, user);

        context.status(201);
        context.json(importedCar);
    }

}
