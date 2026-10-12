package routes;

import controllers.*;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;
import security.EnumRoles;
import security.ISecurityController;
import security.SecurityController;
import security.SecurityRoutes;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final CarController carController;
    private final UserController userController;
    private final ServiceRecordController serviceRecordController;
    private final ModificationController modificationController;
    private final ExpenseController expenseController;
    private final CarImportController carImportController;
    private SecurityRoutes securityRoutes;

    public Routes(
            CarController carController,
            UserController userController,
            ServiceRecordController serviceRecordController,
            ModificationController modificationController,
            ExpenseController expenseController,
            CarImportController carImportController,
            SecurityRoutes securityRoutes
    ) {
        this.carController = carController;
        this.userController = userController;
        this.serviceRecordController = serviceRecordController;
        this.modificationController = modificationController;
        this.expenseController = expenseController;
        this.carImportController = carImportController;
        this.securityRoutes = securityRoutes;
    }

    public EndpointGroup getRoutes() {
        return () -> {
            securityRoutes.getSecurityRoutes().addEndpoints();

            carController.addRoutes();
            userController.addRoutes();
            serviceRecordController.addRoutes();
            modificationController.addRoutes();
            expenseController.addRoutes();

            post(
                    "/service-records/import/{registrationNumber}",
                    ctx -> carImportController.importVehicle(ctx),
                    EnumRoles.USER
            );
        };
    }
}