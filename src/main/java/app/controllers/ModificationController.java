package app.controllers;

import app.dao.ModificationDao;
import app.dao.UserDao;
import app.dto.ModUpdateDTO;
import app.dto.UserDTO;
import app.entities.Modification;
import app.entities.User;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import app.security.EnumRoles;
import app.services.ModificationSecurityService;

import java.util.List;
import java.util.Map;

import static io.javalin.apibuilder.ApiBuilder.*;

public class ModificationController {

    private final ModificationDao modificationDao;
    private final ModificationSecurityService securityService;
    private final UserDao userDao;

    public ModificationController(ModificationDao modificationDao, ModificationSecurityService securityService, UserDao userDao) {
        this.modificationDao = modificationDao;
        this.securityService = securityService;
        this.userDao = userDao;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/modifications/{carId}/total-cost", this::getTotalModificationCost, EnumRoles.USER, EnumRoles.ADMIN);
            get("/modifications", this::getAllModifications, EnumRoles.ADMIN);
            get("/modifications/{id}", this::getModificationById, EnumRoles.USER, EnumRoles.ADMIN);
            get("/modifications/car/{carId}", this::getModificationsByCarId, EnumRoles.USER, EnumRoles.ADMIN);
            post("/cars/{carId}/modifications", this::createModification, EnumRoles.USER, EnumRoles.ADMIN);
            put("/modifications/update/{id}", this::updateModification, EnumRoles.USER, EnumRoles.ADMIN);
            delete("/modifications/delete/{id}", this::deleteModification, EnumRoles.USER, EnumRoles.ADMIN);
        };
    }

    private void getTotalModificationCost(Context context) {
        Integer carId;

        try {
            carId = Integer.parseInt(context.pathParam("carId"));
        } catch (NumberFormatException e) {
            throw new BadRequestResponse("Invalid car ID");
        }

        if (carId <= 0) {
            throw new BadRequestResponse("Car ID must be greater than 0");
        }

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }


        boolean isAdmin = tokenUser.getRoles().equals(EnumRoles.ADMIN);

        Double totalCost = securityService.getTotalModificationCost(carId,tokenUser.getUsername(), isAdmin);

        context.json(totalCost);

    }

    private void getAllModifications(Context context) {
        try {
            List<Modification> allModificatiosn = modificationDao.getAllModifications();
            context.status(200);
            context.json(allModificatiosn);
        } catch (Exception e) {
            context.status(500);
            context.json("Could not retrieve modifications");
        }
    }

    private void createModification(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        Modification modification = context.bodyAsClass(Modification.class);

        boolean isAdmin = tokenUser.getRoles().stream()
                .anyMatch(enumRoles -> enumRoles == EnumRoles.ADMIN);

        Modification createdModification = securityService.createModForUser(carId, tokenUser.getUsername(), isAdmin, modification);
        if (createdModification == null) {
            context.status(404).json("Car not found or access denied");
            return;
        }

        context.status(201);
        context.json(createdModification);
    }

    private void getModificationById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Modification modification =
                modificationDao.findModificationById(id);

        if (modification == null) {
            context.status(404);
            context.result("Modification not found");
            return;
        }

        context.json(modification);
    }

    private void getModificationsByCarId(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        List<Modification> modifications =
                modificationDao.findModificationByCarId(carId);

        context.json(modifications);
    }

    private void updateModification(Context context) {
        Integer modId = Integer.parseInt(context.pathParam("id"));
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        ModUpdateDTO dto = context.bodyAsClass(ModUpdateDTO.class);

        String userName = context.attribute("userName");

        Modification updatedModification = securityService.updateMod(dto, carId, modId, userName);

        if (updatedModification == null) {
            context.status(404).json(
                    Map.of("message",
                            "Bilen eller modification blev ikke fundet")
            );
            return;
        }

        context.json(updatedModification);
    }

    private void deleteModification(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        UserDTO tokenUser = context.attribute("user");
        if (tokenUser == null) {
            throw new UnauthorizedResponse("User is not authenticated");
        }

        boolean isAdmin = tokenUser.getRoles().contains(EnumRoles.ADMIN);

        boolean deleted = securityService.deleteModForCar(id, tokenUser.getUsername(), isAdmin);

        if(!deleted){
            context.status(404).json("Modification not found or access denied");
            return;
        }

        context.status(204);
    }
}