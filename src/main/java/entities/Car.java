package entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String make;
    private String model;
    private int year;
    private int mileage;
    private String registrationNumber;
    private String vin;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}