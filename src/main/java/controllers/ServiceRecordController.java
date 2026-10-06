package controllers;

import dao.ServiceRecordDao;
import entities.Car;
import entities.ServiceRecord;
import entities.User;
import io.javalin.Javalin;
import io.javalin.http.Context;
import services.ImportVehicleService;

import java.util.List;

public class ServiceRecordController {

    private final ServiceRecordDao serviceRecordDao;
    private  final ImportVehicleService importVehicleService;

    public ServiceRecordController(ServiceRecordDao serviceRecordDao, ImportVehicleService importVehicleService) {

        this.serviceRecordDao = serviceRecordDao;
        this.importVehicleService = importVehicleService;
    }

    public void addRoutes(Javalin app) {
        app.post("/service-records/create", ctx -> createServiceRecord(ctx));
        app.post("/service-records/import/{registrationNumber}", ctx -> importVehicle(ctx));
        app.get("/service-records", ctx -> getAllServiceRecords(ctx));
        app.get("/service-records/{id}", ctx -> getServiceRecordById(ctx));
        app.get("/service-records/car/{carId}", ctx -> getServiceRecordsByCarId(ctx));
        app.get("/service-records/car/{carId}/latest", ctx -> getServiceRecordsByCarIdOrderByDate(ctx));
        app.put("/service-records/update/{id}", ctx -> updateServiceRecord(ctx));
        app.delete("/service-records/delete/{id}", ctx -> deleteServiceRecord(ctx));
    }

    private void importVehicle(Context context) {

        String registrationNumber =
                context.pathParam("registrationNumber");

        // Midlertidigt
        User user = new User();
        user.setId(1);

        Car importedCar =
                importVehicleService.importVehicle(
                        registrationNumber,
                        user
                );

        context.status(201);
        context.json(importedCar);
    }

    private void createServiceRecord(Context context) {
        ServiceRecord serviceRecord =
                context.bodyAsClass(ServiceRecord.class);

        ServiceRecord createdServiceRecord =
                serviceRecordDao.createServiceRecord(serviceRecord);

        context.status(201);
        context.json(createdServiceRecord);
    }

    private void getAllServiceRecords(Context context) {
        List<ServiceRecord> serviceRecords =
                serviceRecordDao.findAllServiceRecords();

        context.json(serviceRecords);
    }

    private void getServiceRecordById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        ServiceRecord serviceRecord =
                serviceRecordDao.findServiceRecordById(id);

        if (serviceRecord == null) {
            context.status(404);
            context.result("Service record not found");
            return;
        }

        context.json(serviceRecord);
    }

    private void getServiceRecordsByCarId(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        List<ServiceRecord> serviceRecords =
                serviceRecordDao.findServiceRecordByCarId(carId);

        context.json(serviceRecords);
    }

    private void getServiceRecordsByCarIdOrderByDate(Context context) {
        Integer carId = Integer.parseInt(context.pathParam("carId"));

        List<ServiceRecord> serviceRecords =
                serviceRecordDao.findServiceRecordByCarIdOrderByDateDesc(carId);

        context.json(serviceRecords);
    }

    private void updateServiceRecord(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        ServiceRecord serviceRecord =
                context.bodyAsClass(ServiceRecord.class);

        serviceRecord.setId(id);

        ServiceRecord updatedServiceRecord =
                serviceRecordDao.updateServiceRecord(serviceRecord);

        context.json(updatedServiceRecord);
    }

    private void deleteServiceRecord(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        ServiceRecord serviceRecord =
                serviceRecordDao.findServiceRecordById(id);

        if (serviceRecord == null) {
            context.status(404);
            context.result("Service record not found");
            return;
        }

        serviceRecordDao.deleteServiceRecord(id);

        context.status(204);
    }
}