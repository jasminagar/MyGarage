package app.services;

import app.dao.CarDao;
import app.dao.UserDao;
import app.dto.CarUpdateDTO;
import app.entities.Car;
import app.entities.User;
import app.security.EnumRoles;

public class CarSecurityService {
    private final CarDao carDao;
    private final UserDao userDao;
    private final SecurityService service;

    public CarSecurityService(CarDao carDao, UserDao userDao, SecurityService service) {
        this.carDao = carDao;
        this.userDao = userDao;
        this.service = service;
    }

    public Car updateCarForUser(Integer carId, String userName, CarUpdateDTO updateDTO){
        Car existingCar = carDao.findCarById(carId);
        if (existingCar== null){
            return null;
        }

        if(!existingCar.getId().equals(carId)){
            return null;
        }

        User user = userDao.findUserByUsername(userName);
        if (user == null) {
            return null;
        }

        boolean isAdmin = user.getRoles().equals(EnumRoles.ADMIN);

        if (!isAdmin){
            Car ownedCar = service.findCarForUser(carId, userName);
            if(ownedCar == null){
                throw new SecurityException(
                        "Du har ikke adgang til denne bil");
            }
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
        Car existingCar = carDao.findCarById(carId);
        if (existingCar == null) {
            return false;
        }

        User user = userDao.findUserByUsername(username);
        if (user == null) {
            return false;
        }

        boolean isAdmin = user.getRoles().equals(EnumRoles.ADMIN);

        if (!isAdmin){
            Car ownedCar = service.findCarForUser(carId, username);
            if(ownedCar == null){
                throw new SecurityException(
                        "Du har ikke adgang til denne bil");
            }
        }

        carDao.deleteCar(existingCar);
        return  true;
    }
}
