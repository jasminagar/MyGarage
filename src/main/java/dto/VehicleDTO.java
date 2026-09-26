package dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class VehicleDTO {
    @JsonProperty("registration_number")
    private String registrationNumber;

    @JsonProperty("vin")
    private String vin;

    @JsonProperty("make")
    private String make;

    @JsonProperty("model")
    private String model;

    @JsonProperty("variant")
    private String variant;

    @JsonProperty("model_year")
    private int modelYear;

    @JsonProperty("fuel_type")
    private String fuelType;

    @JsonProperty("engine_volume")
    private int engineVolume;

    @JsonProperty("engine_power")
    private int enginePower;

    @JsonProperty("doors")
    private int doors;

    @JsonProperty("seats")
    private int seats;

    @JsonProperty("total_weight")
    private int totalWeight;

    @JsonProperty("mot_info")
    private MotInfoDTO motInfo;
}
