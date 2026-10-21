package services;

import dao.CarDao;
import dao.UserDao;
import dto.CarUpdateDTO;
import entities.Car;
import entities.User;

public class CarSecurityService {
    private final CarDao carDao;
    private final UserDao userDao;

    public CarSecurityService(CarDao carDao, UserDao userDao) {
        this.carDao = carDao;
        this.userDao = userDao;
    }

    public Car findCarForUser(Integer carId, String userName){
        User user = userDao.findUserByUsername(userName);

        if (user == null) {
            return null;
        }

       return carDao.findCarByIdAndUserId(carId, user.getId());
    }

    public Car updateCarForUser(Integer carId, String userName, CarUpdateDTO updateDTO){
        Car existingCar = findCarForUser(carId, userName);

        if (existingCar == null){
            return null;
        }

        existingCar.setMileage(updateDTO.getMileage());
        existingCar.setRegistrationNumber(updateDTO.getRegistrationNumber());
        existingCar.setEngineVolume(updateDTO.getEngineVolume());
        existingCar.setEnginePower(updateDTO.getEnginePower());
        existingCar.setDoors(updateDTO.getDoors());
        existingCar.setSeats(updateDTO.getSeats());
        existingCar.setTotalWeight(updateDTO.getTotalWeight());

        return carDao.updateCar(existingCar);

    }
    public boolean deleteCarForUser(Integer carId, String username){
        Car existingCar = findCarForUser(carId, username);

        if (existingCar == null) {
            return false;
        }

        carDao.deleteCar(existingCar);
        return  true;
    }
}
