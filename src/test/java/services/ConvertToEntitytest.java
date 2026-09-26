package services;

import dto.MotInfoDTO;
import dto.VehicleDTO;
import entities.Car;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ConvertToEntitytest {

    @Test
    void convertToCarEntity() {

        // Arrange
        MotInfoDTO motInfoDTO = new MotInfoDTO();

        motInfoDTO.setDate("2025-06-20");
        motInfoDTO.setResult("Godkendt");
        motInfoDTO.setMileage(328000);
        motInfoDTO.setNextInspectionDate("2027-06-20");

        VehicleDTO vehicleDTO = new VehicleDTO();

        vehicleDTO.setMake("AUDI");
        vehicleDTO.setModel("A 4 LIMOUSINE");
        vehicleDTO.setVariant("2,0 TDI");
        vehicleDTO.setModelYear(0);

        vehicleDTO.setRegistrationNumber("EC74058");
        vehicleDTO.setVin("WAUZZZ8E67A243220");

        vehicleDTO.setFuelType("Diesel");
        vehicleDTO.setEngineVolume(1968);
        vehicleDTO.setEnginePower(103);

        vehicleDTO.setDoors(4);
        vehicleDTO.setSeats(5);
        vehicleDTO.setTotalWeight(1980);

        vehicleDTO.setMotInfo(motInfoDTO);

        // Act
        ConvertToEntity converter = new ConvertToEntity();

        Car car = converter.convertToCarEntity(vehicleDTO);

        // Assert
        assertEquals("AUDI", car.getMake());
        assertEquals("A 4 LIMOUSINE", car.getModel());
        assertEquals("2,0 TDI", car.getVariant());

        assertEquals(0, car.getYear());

        assertEquals("EC74058", car.getRegistrationNumber());
        assertEquals("WAUZZZ8E67A243220", car.getVin());

        assertEquals("Diesel", car.getFuelType());
        assertEquals(1968, car.getEngineVolume());
        assertEquals(103, car.getEnginePower());

        assertEquals(4, car.getDoors());
        assertEquals(5, car.getSeats());
        assertEquals(1980, car.getTotalWeight());

        assertEquals(328000, car.getMileage());
        assertEquals("2025-06-20", car.getLastInspectionDate().toString());
        assertEquals("Godkendt", car.getInspectionResult());
        assertEquals("2027-06-20", car.getNextInspectionDate().toString());
    }
}
