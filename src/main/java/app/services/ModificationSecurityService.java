package app.services;

import app.dao.CarDao;
import app.dao.ModificationDao;
import app.dao.UserDao;
import app.dto.ModUpdateDTO;
import app.entities.Car;
import app.entities.Modification;
import app.entities.User;
import app.security.EnumRoles;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;

public class ModificationSecurityService {
    private final ModificationDao modificationDao;
    private final UserDao userDao;
    private final CarDao carDao;
    private final SecurityService service;

    public ModificationSecurityService(ModificationDao modificationDao, UserDao userDao, CarDao carDao, SecurityService service) {
        this.modificationDao = modificationDao;
        this.userDao = userDao;
        this.carDao = carDao;
        this.service = service;
    }

    public Double getTotalModificationCost(Integer carId, String username, boolean isAdmin){
        Car car = carDao.findCarById(carId);
        if (car == null) {
            throw new NotFoundResponse(
                    "Car not found with ID: " + carId
            );
        }

        if (!isAdmin){
            Car ownedCar = service.findCarForUser(carId, username);
            if (ownedCar == null) {
                throw new ForbiddenResponse(
                        "You do not have access to car ID: " + carId
                );
            }
        }

        return modificationDao.getTotalCostByCarId(carId);

    }

    public Modification createModForUser(Integer carId, String username, boolean isAdmin, Modification modification) {
        Car car = carDao.findCarById(carId);
        if (car == null) {
            throw new NotFoundResponse(
                    "Car not found with ID: " + carId
            );
        }

        if (!isAdmin) {
            Car ownedCar = service.findCarForUser(carId, username);
            if (ownedCar == null) {
                return null;
            }
        }

        return modificationDao.createModification(carId, modification);
    }

    public Modification updateMod(ModUpdateDTO dto, Integer carId, Integer modId, String userName) {
        Modification modification = modificationDao.findModificationById(modId);
        if (modification == null) {
            return null;
        }

        if (!modification.getCar().getId().equals(carId)) {
            return null;
        }

        User user = userDao.findUserByUsername(userName);
        if (user == null) {
            return null;
        }
        boolean isAdmin = user.getRoles().equals(EnumRoles.ADMIN);

        if(!isAdmin){
            Car car = service.findCarForUser(carId, userName);
            if (car == null) {
                throw new SecurityException(
                        "Du har ikke adgang til denne bil");
            }
        }

        if (dto.getName() != null) {
            modification.setName(dto.getName());
        }

        if (dto.getCategory() != null) {
            modification.setCategory(dto.getCategory());
        }

        if (dto.getDate() != null) {
            modification.setDate(dto.getDate());
        }

        if (dto.getCost() != null) {
            modification.setCost(dto.getCost());
        }

        if (dto.getDescription() != null) {
            modification.setDescription(dto.getDescription());
        }

        return modificationDao.updateModification(modification);

    }

    public boolean deleteModForCar(Integer modificationId, String username, boolean isAdmin) {
        Modification modification = modificationDao.findModificationById(modificationId);
        if (modification == null) {
            return false;
        }

        if (isAdmin) {
            modificationDao.deleteModification(modification);
            return true;
        }

        Integer carId = modification.getCar().getId();

        Car ownedCar = service.findCarForUser(carId, username);
        if (ownedCar == null) {
            return false;
        }

        modificationDao.deleteModification(modification);
        return true;
    }
}
