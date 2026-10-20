package entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDate;

@Data
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Integer id;

    private String make;
    private String model;
    private String variant;

    private int year;
    private int mileage;

    private String registrationNumber;
    private String vin;

    private String fuelType;
    private int engineVolume;
    private int enginePower;

    private int doors;
    private int seats;

    private int totalWeight;

    private LocalDate lastInspectionDate;
    private String inspectionResult;
    private LocalDate nextInspectionDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;

}