import controllers.CarController;
import controllers.ModificationController;
import controllers.ServiceRecordController;
import controllers.UserController;
import dao.CarDao;
import dao.ModificationDao;
import dao.ServiceRecordDao;
import dao.UserDao;
import entities.Modification;
import io.javalin.Javalin;
import security.SecurityController;
import security.SecurityRoutes;
import services.ConvertToEntity;
import services.ImportVehicleService;
import services.VehicleApiReader;

public class Main {
    public static void main(String[] args) {
        Javalin app = Javalin.create();

        CarDao carDao = new CarDao();
        UserDao userDao = new UserDao();
        ServiceRecordDao serviceRecordDao = new ServiceRecordDao();
        ModificationDao modificationDAO = new ModificationDao();
        VehicleApiReader vehicleApiReader = new VehicleApiReader();
        ConvertToEntity converter = new ConvertToEntity();
        ImportVehicleService importVehicleService = new ImportVehicleService(vehicleApiReader, converter, carDao, serviceRecordDao);

        CarController carController = new CarController(carDao);
        UserController userController = new UserController(userDao);
        ServiceRecordController serviceRecordController = new ServiceRecordController(serviceRecordDao, importVehicleService);
        SecurityController securityController = new SecurityController(userDao);
        ModificationController modificationController = new ModificationController(modificationDAO);

        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);

        securityRoutes.addRoutes(app);

        carController.addRoutes(app);
        userController.addRoutes(app);
        serviceRecordController.addRoutes(app);
        modificationController.addRoutes(app);

        app.start(7070);

    }
}

