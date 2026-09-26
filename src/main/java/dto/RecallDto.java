package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class RecallDto {

    @JsonProperty("Manufacturer")
    private String manufacturer;

    @JsonProperty("NHTSACampaignNumber")
    private String nhtsaCampaignNumber;

    @JsonProperty("parkIt")
    private boolean parkIt;

    @JsonProperty("parkOutSide")
    private boolean parkOutSide;

    @JsonProperty("overTheAirUpdate")
    private boolean overTheAirUpdate;

    @JsonProperty("NHTSAActionNumber")
    private String nhtsaActionNumber;

    @JsonProperty("ReportReceivedDate")
    private String reportReceivedDate;

    @JsonProperty("Component")
    private String component;

    @JsonProperty("Summary")
    private String summary;

    @JsonProperty("Consequence")
    private String consequence;

    @JsonProperty("Remedy")
    private String remedy;

    @JsonProperty("Notes")
    private String notes;

    @JsonProperty("ModelYear")
    private String modelYear;

    @JsonProperty("Make")
    private String make;

    @JsonProperty("Model")
    private String model;


}