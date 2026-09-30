package services;

import dto.MotInfoDTO;
import dto.VehicleDTO;
import entities.Car;
import entities.ServiceRecord;

import java.time.LocalDate;

public class ConvertToEntity {

    public Car convertToCarEntity(VehicleDTO vehicleDTO) {

        Car car = new Car();

        car.setMake(vehicleDTO.getMake());
        car.setModel(vehicleDTO.getModel());
        car.setVariant(vehicleDTO.getVariant());
        car.setYear(vehicleDTO.getModelYear());

        car.setRegistrationNumber(vehicleDTO.getRegistrationNumber());
        car.setVin(vehicleDTO.getVin());

        car.setFuelType(vehicleDTO.getFuelType());
        car.setEngineVolume(vehicleDTO.getEngineVolume());
        car.setEnginePower(vehicleDTO.getEnginePower());

        car.setDoors(vehicleDTO.getDoors());
        car.setSeats(vehicleDTO.getSeats());
        car.setTotalWeight(vehicleDTO.getTotalWeight());

        if (vehicleDTO.getMotInfo() != null) {
            if (vehicleDTO.getMotInfo() != null) {
                car.setMileage(vehicleDTO.getMotInfo().getMileage());
                car.setLastInspectionDate(
                        LocalDate.parse(vehicleDTO.getMotInfo().getDate())
                );
                car.setInspectionResult(
                        vehicleDTO.getMotInfo().getResult()
                );
                car.setNextInspectionDate(
                        LocalDate.parse(vehicleDTO.getMotInfo().getNextInspectionDate())
                );
            }
        }

        return car;
    }

    public ServiceRecord convertToServiceRecord(VehicleDTO vehicleDTO, Car car) {
        if (vehicleDTO.getMotInfo() == null) {
            return null;
        }

        MotInfoDTO motInfo = vehicleDTO.getMotInfo();

        ServiceRecord serviceRecord = new ServiceRecord();

        serviceRecord.setDate(
                LocalDate.parse(motInfo.getDate())
        );

        serviceRecord.setType(
                motInfo.getType()
        );

        serviceRecord.setMileage(
                motInfo.getMileage()
        );

        serviceRecord.setDescription(
                motInfo.getResult()
        );

        serviceRecord.setCar(car);

        return serviceRecord;
    }
}
