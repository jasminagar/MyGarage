import controllers.*;
import dao.*;
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
        ExpenseDao expenseDao = new ExpenseDao();
        VehicleApiReader vehicleApiReader = new VehicleApiReader();
        ConvertToEntity converter = new ConvertToEntity();
        ImportVehicleService importVehicleService = new ImportVehicleService(vehicleApiReader, converter, carDao, serviceRecordDao);

        CarController carController = new CarController(carDao);
        UserController userController = new UserController(userDao);
        ServiceRecordController serviceRecordController = new ServiceRecordController(serviceRecordDao, importVehicleService);
        SecurityController securityController = new SecurityController(userDao);
        ModificationController modificationController = new ModificationController(modificationDAO);
        ExpenseController expenseController = new ExpenseController(expenseDao);

        SecurityRoutes securityRoutes = new SecurityRoutes(securityController);

        securityRoutes.addRoutes(app);

        carController.addRoutes(app);
        userController.addRoutes(app);
        serviceRecordController.addRoutes(app);
        modificationController.addRoutes(app);
        expenseController.addRoutes(app);

        app.start(7070);

    }
}

