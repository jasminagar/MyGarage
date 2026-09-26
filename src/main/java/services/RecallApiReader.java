package services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.RecallResponseDto;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class RecallApiReader {

    String url = "https://api.nhtsa.gov/recalls/recallsByVehicle"
            + "?make=acura"
            + "&model=rdx"
            + "&modelYear=2012";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String apiReader() {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();
            HttpResponse<String> httpResponse =
                    httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != 200) {
                throw new RuntimeException("Failed: " + httpResponse.statusCode());
            }
            return httpResponse.body();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public RecallResponseDto convertFromJson(String json) {
        try {
            return objectMapper.readValue(json, RecallResponseDto.class);
        } catch (JsonProcessingException e){
            throw new RuntimeException(e);
        }
    }
}
