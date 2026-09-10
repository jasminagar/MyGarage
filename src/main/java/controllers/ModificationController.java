package controllers;

import dao.ModificationDao;
import entities.Modification;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class ModificationController {

    private final ModificationDao modificationDao;

    public ModificationController(ModificationDao modificationDao) {
        this.modificationDao = modificationDao;
    }

    public void addRoutes(Javalin app) {
        app.post("/modifications/create", this::createModification);
        app.get("/modifications", this::getAllModifications);
        app.get("/modifications/{id}", this::getModificationById);
        app.get("/modifications/car/{carId}", this::getModificationsByCarId);
        app.put("/modifications/update/{id}", this::updateModification);
        app.delete("/modifications/delete/{id}", this::deleteModification);
    }

    private void createModification(Context context) {
        Modification modification =
                context.bodyAsClass(Modification.class);

        Modification createdModification =
                modificationDao.createModification(modification);

        context.status(201);
        context.json(createdModification);
    }

    private void getAllModifications(Context context) {
        List<Modification> modifications =
                modificationDao.findAllModifications();

        context.json(modifications);
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
        Integer id = Integer.parseInt(context.pathParam("id"));

        Modification modification =
                context.bodyAsClass(Modification.class);

        modification.setId(id);

        Modification updatedModification =
                modificationDao.updateModification(modification);

        context.json(updatedModification);
    }

    private void deleteModification(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        Modification modification =
                modificationDao.findModificationById(id);

        if (modification == null) {
            context.status(404);
            context.result("Modification not found");
            return;
        }

        modificationDao.deleteModification(id);

        context.status(204);
    }
}