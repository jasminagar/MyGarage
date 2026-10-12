package controllers;

import dao.ServiceRecordDao;
import entities.Car;
import entities.ServiceRecord;
import entities.User;
import io.javalin.Javalin;
import io.javalin.http.Context;
import services.ImportVehicleService;

import java.util.List;

import static io.javalin.apibuilder.ApiBuilder.delete;
import static io.javalin.apibuilder.ApiBuilder.get;

public class ServiceRecordController {

    private final ServiceRecordDao serviceRecordDao;
    private  final ImportVehicleService importVehicleService;

    public ServiceRecordController(ServiceRecordDao serviceRecordDao, ImportVehicleService importVehicleService) {

        this.serviceRecordDao = serviceRecordDao;
        this.importVehicleService = importVehicleService;
    }

    public void addRoutes() {
        get("/service-records", ctx -> getAllServiceRecords(ctx));
        get("/service-records/{id}", ctx -> getServiceRecordById(ctx));
        get("/service-records/car/{carId}", ctx -> getServiceRecordsByCarId(ctx));
        get("/service-records/car/{carId}/latest", ctx -> getServiceRecordsByCarIdOrderByDate(ctx));
        delete("/service-records/delete/{id}", ctx -> deleteServiceRecord(ctx));
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