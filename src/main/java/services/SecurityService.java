package services;

import dao.CarDao;
import dao.UserDao;
import entities.Car;
import entities.User;

public class SecurityService {
    private final UserDao userDao;
    private final CarDao carDao;

    public SecurityService(UserDao userDao, CarDao carDao) {
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
}
