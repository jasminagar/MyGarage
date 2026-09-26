package services;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class VehicleApiReaderTest {

    @Test
    void testEnvironmentVariable() {
        String apiKey = System.getenv("VEHICLE_API_KEY");

        System.out.println("API key is null: " + (apiKey == null));
    }

    @Test
    void getVehicleShouldReturnJson() {
        VehicleApiReader vehicleApiReader = new VehicleApiReader();

        String result = vehicleApiReader.getVehicle("ec74058");

        assertNotNull(result);
        assertFalse(result.isEmpty());

        System.out.println(result);
    }
}
