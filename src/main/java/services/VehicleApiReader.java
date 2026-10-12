package services;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.VehicleDTO;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class VehicleApiReader {
    //Oprettet objectmapper objekt for at konvertere json svar til dto.
    ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = "https://v1.motorapi.dk/vehicles/";
    String apiKey = System.getenv("VEHICLE_API_KEY");

    public String getVehicle(String regristraionNumber){
        try{
            //Opretter en httpclient. Den er ansvarlig for at sende forespørgsler til, og modtage svar fra  eksterne api.
            HttpClient httpClient = HttpClient.newHttpClient();

            //Bygger httpforespørgsel med newbuilder.
            //Gør det muligt at bygge forespørgsel trin for trin inden den bygges færdig.
            HttpRequest httpRequest = HttpRequest.newBuilder()
                    //Angiver adressen forespørgslen skal sendes til.
                    .uri(new URI(baseUrl + regristraionNumber))
                    //Tlføjer header med navn. Indeholder apinøgle som motorapi skal bruge ti at godkende forespørgsel.
                    .header("X-AUTH-TOKEN", apiKey)
                    .GET().build();

            HttpResponse<String> httpResponse =
                    httpClient.send( //Sender forespørgsel og venter på svar.
                            httpRequest, //Den forspørgsel jeg byggede før.
                            HttpResponse.BodyHandlers.ofString() //Fortæller java at svarets body skal læses som string.
                    );

            if (httpResponse.statusCode() != 200){
                throw new RuntimeException("Failed: " + httpResponse.statusCode());
            }

            //Returnerer selve indholdet af http svaret som string.
            //Json data i dette tilfælde.
            return httpResponse.body();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public VehicleDTO convertFromJson(String json){
        try{
            //readvalue læser indhold af json og konverter, vehicledto.class fortæller jackson hvilken java klasse.
            return objectMapper.readValue(json, VehicleDTO.class);
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
