package services;

import dto.VehicleDTO;
import entities.Car;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class VehicleApiIntegrationTest {

    @Test
    void testEnvironmentVariable() {
        String apiKey = System.getenv("VEHICLE_API_KEY");

        System.out.println("API key is null: " + (apiKey == null));
    }

    @Test
    void getVehicleAndConvertToCar() {

        // Arrange
        VehicleApiReader apiReader = new VehicleApiReader();
        ConvertToEntity converter = new ConvertToEntity();

        String registrationNumber = "EC74058";

        // Act
        String json = apiReader.getVehicle(registrationNumber);

        VehicleDTO vehicleDTO = apiReader.convertFromJson(json);

        Car car = converter.convertToCarEntity(vehicleDTO);

        // Assert
        assertNotNull(json);
        assertNotNull(vehicleDTO);
        assertNotNull(car);

        assertEquals("EC74058", car.getRegistrationNumber());
        assertEquals("AUDI", car.getMake());
        assertEquals("A 4 LIMOUSINE", car.getModel());
        assertEquals("2,0 TDI", car.getVariant());
        assertEquals("WAUZZZ8E67A243220", car.getVin());

        assertEquals("Diesel", car.getFuelType());
        assertEquals(1968, car.getEngineVolume());
        assertEquals(103, car.getEnginePower());

        assertEquals(4, car.getDoors());
        assertEquals(5, car.getSeats());
        assertEquals(1980, car.getTotalWeight());

        assertEquals(328000, car.getMileage());
        assertEquals("Godkendt", car.getInspectionResult());
    }
}