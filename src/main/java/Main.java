import config.SessionConfig;
import config.ThymeleafConfig;
import controllers.CarController;
import controllers.UserController;
import dao.CarDao;
import dao.UserDao;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinThymeleaf;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create().start(7070);

        CarDao carDao = new CarDao();
        UserDao userDao = new UserDao();

        CarController carController = new CarController(carDao);
        UserController userController = new UserController(userDao);

        carController.addRoutes(app);
        userController.addRoutes(app);


    }
}
