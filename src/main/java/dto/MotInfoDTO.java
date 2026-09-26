package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
public class MotInfoDTO {

    private String type;

    private String date;

    private String result;

    private String status;

    @JsonProperty("status_date")
    private String statusDate;

    private int mileage;

    @JsonProperty("next_inspection_date")
    private String nextInspectionDate;
}