package app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import app.dto.VehicleDTO;
import io.javalin.http.BadGatewayResponse;
import io.javalin.http.NotFoundResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class MotorApiReader {

    ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://v1.motorapi.dk/vehicles/";
    String apiKey = System.getenv("VEHICLE_API_KEY");

    public String getVehicle(String regristraionNumber){
        try{
            HttpClient httpClient = HttpClient.newHttpClient();

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(baseUrl + regristraionNumber))
                    .header("X-AUTH-TOKEN", apiKey)
                    .GET().build();

            HttpResponse<String> httpResponse =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (httpResponse.statusCode() == 404) {
                throw new NotFoundResponse("Vehicle not found");
            }

            if (httpResponse.statusCode() != 200) {
                throw new BadGatewayResponse(
                        "External vehicle API returned status "
                                + httpResponse.statusCode()
                );
            }

            return httpResponse.body();
        } catch (NotFoundResponse | BadGatewayResponse e) {
            throw e;
        } catch (Exception e) {
            throw new BadGatewayResponse("Could not communicate with vehicle API");
        }
    }

    public VehicleDTO convertFromJson(String json){
        try{
            return objectMapper.readValue(json, VehicleDTO.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
