package app.routes;

import app.controllers.*;
import io.javalin.apibuilder.ApiBuilder;
import io.javalin.apibuilder.EndpointGroup;

import static java.nio.file.Paths.get;

public class Routes {

    private final CarController carController;
    private final UserController userController;
    private final InspectionRecordController serviceRecordController;
    private final ModificationController modificationController;
    private final ExpenseController expenseController;
    private final CarImportController carImportController;
    private SecurityRoutes securityRoutes;

    public Routes(CarController carController, UserController userController, InspectionRecordController serviceRecordController, ModificationController modificationController, ExpenseController expenseController, CarImportController carImportController, SecurityRoutes securityRoutes) {
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
            ApiBuilder.get("/", ctx -> {ctx.status(200);ctx.contentType("text/plain");ctx.result("Welcome to MyGarage API!");});
            securityRoutes.getSecurityRoutes().addEndpoints();
            expenseController.getRoutes().addEndpoints();
            carController.getRoutes().addEndpoints();
            modificationController.getRoutes().addEndpoints();
            serviceRecordController.getRoutes().addEndpoints();
            userController.getRoutes().addEndpoints();
            carImportController.getRoutes().addEndpoints();
        };
    }
}
