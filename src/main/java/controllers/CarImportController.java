package controllers;

import dao.UserDao;
import dto.UserDTO;
import entities.Car;
import entities.User;
import io.javalin.Javalin;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import security.EnumRoles;
import security.SecurityRoutes;
import services.ImportVehicleService;

import static io.javalin.apibuilder.ApiBuilder.post;

public class CarImportController {
    private final ImportVehicleService importVehicleService;
    private final UserDao userDao;

    public CarImportController(ImportVehicleService importVehicleService, UserDao userDao) {
        this.importVehicleService = importVehicleService;
        this.userDao = userDao;
    }

    public void addRoutes() {
        post(
                "/service-records/import/{registrationNumber}",
                ctx -> importVehicle(ctx),
                EnumRoles.USER
        );
    }

    public void importVehicle(Context context) {
        //Henter nummeplade fra url.
        String registrationNumber =
                context.pathParam("registrationNumber");

        UserDTO tokenUser = context.attribute("user");

        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

       User user = userDao.findUserByUsername(tokenUser.getUsername());

        //Jeg sender to parametrer med. Registreingsnummeret på bilen der skal importeres.
        //Brugeren der skal tilknyttes bilen.
        Car importedCar =
                importVehicleService.importVehicle(
                        registrationNumber,
                        user
                );

        context.status(201);
        //Sender det oprettede car objekt tilbage til klienten i json format.
        context.json(importedCar);
    }

}
