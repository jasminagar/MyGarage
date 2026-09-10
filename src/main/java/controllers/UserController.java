package controllers;

import dao.UserDao;
import entities.User;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.List;

public class UserController {
    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    public void addRoutes(Javalin app){
        app.post("/user/create", ctx -> createUser(ctx));
        app.get("/users/{id}", ctx -> getUserById(ctx));
        app.put("/users/update/{id}", ctx -> updateUser(ctx));
        app.delete("users/delete", ctx -> deleteUser(ctx));
    }

    private void createUser(Context context){
        User user  = context.bodyAsClass(User.class);
        User createdUser = userDao.createUser(user);
        context.status(201);
        context.json(createdUser);
    }

    private void getUserById(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        User user = userDao.findUserById(id);

        if (user == null) {
            context.status(404);
            context.result("User not found");
            return;
        }

        context.json(user);
    }

    private void updateUser(Context context) {
        Integer id = Integer.parseInt(context.pathParam("id"));

        User user = context.bodyAsClass(User.class);
        user.setId(id);

        User updatedUser = userDao.updateUser(user);

        context.json(updatedUser);
    }

    private void deleteUser(Context context) {
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
