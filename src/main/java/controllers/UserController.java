package controllers;

import dao.UserDao;
import entities.User;
import io.javalin.apibuilder.EndpointGroup;
import io.javalin.http.Context;
import security.EnumRoles;

import static io.javalin.apibuilder.ApiBuilder.*;

public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            post("/users/delete", this::deleteUser, EnumRoles.ADMIN);
            post("/users/update/{id}", this::updateUser, EnumRoles.USER, EnumRoles.ADMIN);
            post("/users/{id}", this::getUserById, EnumRoles.ADMIN);
        };
    }

    public void getUserById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        User user = userDao.findUserById(id);

        if (user == null) {
            context.status(404);
            context.result("User not found");
            return;
        }

        context.json(user);
    }

    public void updateUser(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        User user = context.bodyAsClass(User.class);
        user.setId(id);

        User updatedUser = userDao.updateUser(user);

        context.json(updatedUser);
    }

    public void deleteUser(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        User user = userDao.findUserById(id);

        if (user == null) {
            context.status(404);
            context.result("User not found");
            return;
        }

        userDao.deleteUser(user);

        context.status(204);
    }
}
