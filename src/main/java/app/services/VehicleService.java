package app.services;

import app.dao.CarDao;
import app.dao.InspectionRecordDao;
import app.dto.VehicleDTO;
import app.entities.Car;
import app.entities.InspectionRecord;
import app.entities.User;
import io.javalin.http.BadGatewayResponse;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.InternalServerErrorResponse;
import io.javalin.http.UnauthorizedResponse;

public class VehicleService {

    private final MotorApiReader apiReader;
    private final ConvertToEntity converter;
    private final CarDao carDao;
    private final InspectionRecordDao serviceRecordDao;

    public VehicleService(MotorApiReader apiReader, ConvertToEntity converter, CarDao carDao, InspectionRecordDao serviceRecordDao) {
        this.apiReader = apiReader;
        this.converter = converter;
        this.carDao = carDao;
        this.serviceRecordDao = serviceRecordDao;
    }

    public Car importVehicle(String registrationNumber, User user) {
        if (registrationNumber == null ||
                !registrationNumber.matches("[A-Z]{2}\\d{5}")) {
            throw new BadRequestResponse("Invalid registration number format");
        }

        if (user == null) {
            throw new UnauthorizedResponse(
                    "User not found or not authenticated"
            );
        }

        String json = apiReader.getVehicle(registrationNumber);
        if (json == null || json.isBlank()) {
            throw new BadGatewayResponse(
                    "Vehicle API returned an empty response"
            );
        }
        VehicleDTO vehicleDTO = apiReader.convertFromJson(json);
        if (vehicleDTO == null) {
            throw new BadGatewayResponse(
                    "Unable to parse vehicle API response"
            );
        }

        Car car = converter.convertToCarEntity(vehicleDTO);
        if (car == null) {
            throw new InternalServerErrorResponse(
                    "Failed to convert vehicle data"
            );
        }
        car.setUser(user);
        Car savedCar = carDao.createCar(car);
        if (savedCar == null) {
            throw new InternalServerErrorResponse(
                    "Failed to save imported car"
            );
        }

        InspectionRecord serviceRecord = converter.convertToServiceRecord(vehicleDTO, savedCar);

        if (serviceRecord != null) {
            serviceRecordDao.createServiceRecord(serviceRecord);
        }

        return savedCar;
    }
}
