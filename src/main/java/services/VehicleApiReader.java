package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.VehicleDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VehicleApiReader {
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

            System.out.println(apiKey == null);

            HttpResponse<String> httpResponse =
                    httpClient.send(
                            httpRequest,
                            HttpResponse.BodyHandlers.ofString()
                    );

            if (httpResponse.statusCode() != 200){
                throw new RuntimeException("Failed: " + httpResponse.statusCode());
            }

            return httpResponse.body();
        } catch (Exception e){
            throw new RuntimeException(e);
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
