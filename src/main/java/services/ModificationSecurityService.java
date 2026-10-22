package services;

import dao.CarDao;
import dao.ModificationDao;
import dao.UserDao;
import dto.ModUpdateDTO;
import entities.Car;
import entities.Modification;
import entities.User;
import security.EnumRoles;

public class ModificationSecurityService {
    private final ModificationDao modificationDao;
    private final UserDao userDao;
    private final CarDao carDao;

    public ModificationSecurityService(ModificationDao modificationDao, UserDao userDao, CarDao carDao) {
        this.modificationDao = modificationDao;
        this.userDao = userDao;
        this.carDao = carDao;
    }

    public Car findCarForUser(Integer carId, String userName) {
        User user = userDao.findUserByUsername(userName);

        if (user == null) {
            return null;
        }

        return carDao.findCarByIdAndUserId(carId, user.getId());
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
            Car car = findCarForUser(carId, userName);
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

    public Modification createModForUser(Integer carId, String username, boolean isAdmin, Modification modification) {
        Car car = carDao.findCarById(carId);
        if (car == null) {
            return null;
        }

        if (!isAdmin) {
            Car ownedCar = findCarForUser(carId, username);

            if (ownedCar == null) {
                return null;
            }
        }

        return modificationDao.createModification(carId, modification);
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

        Car ownedCar = findCarForUser(carId, username);
        if (ownedCar == null) {
            return false;
        }

        modificationDao.deleteModification(modification);
        return true;
    }
}
