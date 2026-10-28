package app.controllers;

import app.dao.ServiceRecordDao;
import app.entities.ServiceRecord;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import app.security.EnumRoles;
import app.services.VehicleService;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;

public class ServiceRecordController {

    private final ServiceRecordDao serviceRecordDao;
    private final VehicleService importVehicleService;

    public ServiceRecordController(ServiceRecordDao serviceRecordDao, VehicleService importVehicleService) {

        this.serviceRecordDao = serviceRecordDao;
        this.importVehicleService = importVehicleService;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            get("/service-records", ctx -> getAllServiceRecords(ctx), EnumRoles.ADMIN);
            get("/service-records/{id}", ctx -> getServiceRecordById(ctx), EnumRoles.USER, EnumRoles.ADMIN);
            get("/service-records/car/{carId}", ctx -> getServiceRecordsByCarId(ctx), EnumRoles.USER, EnumRoles.ADMIN);
            get("/service-records/car/{carId}/latest", ctx -> getServiceRecordsByCarIdOrderByDate(ctx), EnumRoles.USER, EnumRoles.ADMIN);
            delete("/service-records/delete/{id}", ctx -> deleteServiceRecord(ctx), EnumRoles.USER, EnumRoles.ADMIN);
        };
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