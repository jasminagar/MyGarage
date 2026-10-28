package app.config;

import app.controllers.CarController;
import app.controllers.UserController;
import app.controllers.ServiceRecordController;
import app.controllers.ModificationController;
import app.controllers.ExpenseController;
import app.controllers.CarImportController;
import app.dao.UserDao;
import app.dao.CarDao;
import app.dao.ServiceRecordDao;
import app.dao.ModificationDao;
import app.dao.ExpenseDao;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import app.routes.Routes;
import app.security.SecurityController;
import app.routes.SecurityRoutes;
import app.services.*;

public class ApplicationConfig {

    private static Routes routes;
    private static UserDao userDao;
    private static SecurityController securityController;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApplicationConfig.class);

    private ApplicationConfig() {
    }

    public static void configuration(JavalinConfig config) {
        config.router.contextPath = "/api/v1";
        config.http.defaultContentType = "application/json";
        config.router.apiBuilder(routes.getRoutes());

    }
    public static Javalin startServer(int port) {
        userDao = new UserDao();
        securityController = new SecurityController(userDao);
        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);

        CarDao carDao = new CarDao();
        ServiceRecordDao serviceRecordDao = new ServiceRecordDao();
        ModificationDao modificationDao = new ModificationDao();
        ExpenseDao expenseDao = new ExpenseDao();

        VehicleApiReader vehicleApiReader = new VehicleApiReader();
        ConvertToEntity converter = new ConvertToEntity();

        ImportVehicleService importVehicleService =
                new ImportVehicleService(
                        vehicleApiReader,
                        converter,
                        carDao,
                        serviceRecordDao
                );

        SecurityService service = new SecurityService(userDao, carDao);

        CarSecurityService securityService = new CarSecurityService(carDao, userDao,service);

        CarController carController = new CarController(carDao, userDao, securityService);
        UserController userController = new UserController(userDao);

        ServiceRecordController serviceRecordController =
                new ServiceRecordController(
                        serviceRecordDao,
                        importVehicleService
                );

        ModificationSecurityService securityService1 = new ModificationSecurityService(modificationDao, userDao, carDao, service);
        ModificationController modificationController =
                new ModificationController(modificationDao, securityService1);

        ExpenseSecurityService expenseSecurityService = new ExpenseSecurityService(expenseDao,carDao,userDao, service);
        ExpenseController expenseController =
                new ExpenseController(expenseDao, expenseSecurityService);

        CarImportController carImportController =
                new CarImportController(importVehicleService, userDao);

        routes = new Routes(
                carController,
                userController,
                serviceRecordController,
                modificationController,
                expenseController,
                carImportController,
                securityRoutes
        );

        Javalin app = Javalin.create(ApplicationConfig::configuration);
        app.beforeMatched(securityController::authenticate);
        app.beforeMatched(securityController::authorize);
        app.get("/", ctx -> ctx.result("Velkommen"));
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
