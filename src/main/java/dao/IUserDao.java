package dao;

import entities.User;

public interface IUserDao {

    User createUser(User user);

    User findUserById(Integer id);

    User updateUser(User user);

    void deleteUser(User User);
}
