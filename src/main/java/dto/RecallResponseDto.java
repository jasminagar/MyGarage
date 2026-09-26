package dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecallResponseDto {

    @JsonProperty("Count")
    private int count;

    @JsonProperty("Message")
    private String message;

    @JsonProperty("results")
    private List<RecallDto> results;

}