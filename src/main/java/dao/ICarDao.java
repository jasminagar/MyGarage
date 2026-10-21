package dao;

import entities.Car;

import java.util.List;

public interface ICarDao {

    Car createCar(Car car);

    Car findCarByIdAndUserId(Integer carId, Integer userId);

    void deleteCar(Car car);

    Car updateCar(Car car);

    List<Car> findAllcars();

    List<Car> findCarByUserId(Integer userId);

}
