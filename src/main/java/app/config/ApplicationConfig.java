package app.config;

import app.controllers.CarController;
import app.controllers.UserController;
import app.controllers.InspectionRecordController;
import app.controllers.ModificationController;
import app.controllers.ExpenseController;
import app.controllers.CarImportController;
import app.dao.UserDao;
import app.dao.CarDao;
import app.dao.InspectionRecordDao;
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
        AppConfig appConfig = new AppConfig();
        TokenService tokenService = new TokenService(appConfig);
        securityController = new SecurityController(userDao, tokenService);
        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);

        CarDao carDao = new CarDao();
        InspectionRecordDao serviceRecordDao = new InspectionRecordDao();
        ModificationDao modificationDao = new ModificationDao();
        ExpenseDao expenseDao = new ExpenseDao();

        MotorApiReader vehicleApiReader = new MotorApiReader();
        ConvertToEntity converter = new ConvertToEntity();

        VehicleService importVehicleService =
                new VehicleService(
                        vehicleApiReader,
                        converter,
                        carDao,
                        serviceRecordDao
                );

        SecurityService service = new SecurityService(userDao, carDao);

        CarSecurityService securityService = new CarSecurityService(carDao, userDao,service);

        CarController carController = new CarController(carDao, userDao, securityService);
        UserController userController = new UserController(userDao);

        InspectionRecordController serviceRecordController =
                new InspectionRecordController(
                        serviceRecordDao,
                        importVehicleService
                );

        ModificationSecurityService securityService1 = new ModificationSecurityService(modificationDao, userDao, carDao, service);
        ModificationController modificationController =
                new ModificationController(modificationDao, securityService1, userDao);

        ExpenseSecurityService expenseSecurityService = new ExpenseSecurityService(expenseDao,carDao,userDao, service);
        ExpenseController expenseController =
                new ExpenseController(expenseDao, expenseSecurityService);

        CarImportController carImportController =
                new CarImportController(importVehicleService, userDao);

        AuthenticationService authenticate = new AuthenticationService(tokenService);
        AuthorizationService authorize = new AuthorizationService();

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
        app.beforeMatched(authenticate::authenticate);
        app.beforeMatched(authorize::authorize);
//        app.get("/", ctx -> ctx.result("Welcome"));
        app.start(port);

        return app;
    }
}
