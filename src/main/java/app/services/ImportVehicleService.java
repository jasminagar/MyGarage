package app.services;

import app.dao.CarDao;
import app.dao.ServiceRecordDao;
import app.dto.VehicleDTO;
import app.entities.Car;
import app.entities.ServiceRecord;
import app.entities.User;

public class ImportVehicleService {

    private final VehicleApiReader apiReader;
    private final ConvertToEntity converter;
    private final CarDao carDao;
    private final ServiceRecordDao serviceRecordDao;

    public ImportVehicleService(VehicleApiReader apiReader, ConvertToEntity converter, CarDao carDao, ServiceRecordDao serviceRecordDao) {
        this.apiReader = apiReader;
        this.converter = converter;
        this.carDao = carDao;
        this.serviceRecordDao = serviceRecordDao;
    }

    public Car importVehicle(String registrationNumber, User user) {
        String json = apiReader.getVehicle(registrationNumber);
        VehicleDTO vehicleDTO = apiReader.convertFromJson(json);

        Car car = converter.convertToCarEntity(vehicleDTO);
        car.setUser(user);
        Car savedCar = carDao.createCar(car);

        ServiceRecord serviceRecord = converter.convertToServiceRecord(vehicleDTO, savedCar);

        if (serviceRecord != null) {
            serviceRecordDao.createServiceRecord(serviceRecord);
        }

        return savedCar;
    }
}
