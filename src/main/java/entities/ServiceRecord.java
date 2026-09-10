package entities;

import entities.Car;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate date;
    private String type;
    private int mileage;
    private double cost;
    private String description;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;
}