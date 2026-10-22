package dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ExpenseUpdateDTO {

    private LocalDate date;
    private String category;
    private double amount;
    private String description;
}
