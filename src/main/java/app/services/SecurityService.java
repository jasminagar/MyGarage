package app.services;

import app.dao.CarDao;
import app.dao.UserDao;
import app.entities.Car;
import app.entities.User;

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
