package config;

import controllers.CarController;
import controllers.UserController;
import controllers.ServiceRecordController;
import controllers.ModificationController;
import controllers.ExpenseController;
import controllers.CarImportController;
import dao.UserDao;
import dao.CarDao;
import dao.ServiceRecordDao;
import dao.ModificationDao;
import dao.ExpenseDao;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import routes.Routes;
import security.SecurityController;
import security.SecurityRoutes;
import services.ConvertToEntity;
import services.ImportVehicleService;
import services.VehicleApiReader;

//Samler konfig af Javalin server. Opretter nødvendige objekter, forbinder dao til controller.
//Registrerer routes og starter server.
public class ApplicationConfig {

    private static Routes routes;
    private static UserDao userDao;
    private static SecurityController securityController;

    private static final Logger LOGGER =
            LoggerFactory.getLogger(ApplicationConfig.class);

    private ApplicationConfig() {
    }

    //Konfigurerer Javalin når serveren starter.
    public static void configuration(JavalinConfig config) { //Bruges blandt andet til routing og http.
        //contextPath - Sætter fælles base for appliaktionens routes.
        //Gør det muligt at gruppere routes under versionssti.
        config.router.contextPath = "/api/v1";
        //Standard indholdstype for http responser.
        config.http.defaultContentType = "application/json";
        //apiBuilder - Registerer routes i Javalin.
        config.router.apiBuilder(routes.getRoutes());//Henter Endpointgroup i Routes klasse.

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

        CarController carController = new CarController(carDao);
        UserController userController = new UserController(userDao);

        ServiceRecordController serviceRecordController =
                new ServiceRecordController(
                        serviceRecordDao,
                        importVehicleService
                );

        ModificationController modificationController =
                new ModificationController(modificationDao);

        ExpenseController expenseController =
                new ExpenseController(expenseDao);

        CarImportController carImportController =
                new CarImportController(importVehicleService, userDao);
        //Samler controllerne i et routes objekt.
        //Routes får de objekter den skal bruge til at registere endpoints.
        routes = new Routes(
                carController,
                userController,
                serviceRecordController,
                modificationController,
                expenseController,
                carImportController,
                securityRoutes
        );
        //Opretter Javalin server med config metode jeg har lavet.
        Javalin app = Javalin.create(ApplicationConfig::configuration);
        //Registerer jwt middleware.
        //beforeMatched -Efter route er matchet, men før udførelsen.
        //Tester gyldig jwt token, og henter brugeroplysninger fra token.
        app.beforeMatched(securityController::authenticate);
        //Kontrollerer om brugeren har den rigtige rolle.
        app.beforeMatched(securityController::authorize);
        app.start(port);

        return app;
    }

    public static void stopServer(Javalin app) {
        app.stop();
    }
}
