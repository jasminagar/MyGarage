package app.controllers;

import app.dao.UserDao;
import app.dto.UserDTO;
import app.entities.Car;
import app.entities.User;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import app.security.EnumRoles;
import app.services.VehicleService;

import static io.javalin.apibuilder.ApiBuilder.post;

public class CarImportController {
    private final VehicleService importVehicleService;
    private final UserDao userDao;

    public CarImportController(VehicleService importVehicleService, UserDao userDao) {
        this.importVehicleService = importVehicleService;
        this.userDao = userDao;
    }

    public EndpointGroup getRoutes(){
        return() ->{
            post("/service-records/import/{registrationNumber}", this::importVehicle, EnumRoles.USER, EnumRoles.ADMIN);
        };
    }

    private void importVehicle(Context context) {
        String registrationNumber = context.pathParam("registrationNumber");

        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse(
                    "User is not authenticated"
            );
        }

        User user = userDao.findUserByUsername(tokenUser.getUsername());

        if (user == null) {
            throw new UnauthorizedResponse(
                    "Authenticated user no longer exists"
            );
        }

        Car importedCar = importVehicleService.importVehicle(
                registrationNumber,
                user
        );

        context.status(201).json(importedCar);
    }
}
