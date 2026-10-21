package dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ModUpdateDTO {
    private String name;
    private String category;
    private LocalDate date;
    private Double cost;
    private String description;
}
