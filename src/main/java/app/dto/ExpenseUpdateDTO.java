package app.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseUpdateDTO {

    private LocalDate date;
    private String category;
    private Double amount;
    private String description;
}
