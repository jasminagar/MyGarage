package services;

import dto.VehicleDTO;
import entities.Car;

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
}
