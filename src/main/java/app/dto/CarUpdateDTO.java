package app.dto;

import lombok.Data;

@Data
public class CarUpdateDTO {

    private int mileage;
    private String registrationNumber;

    private int engineVolume;
    private int enginePower;

    private int doors;
    private int seats;

    private int totalWeight;
}
