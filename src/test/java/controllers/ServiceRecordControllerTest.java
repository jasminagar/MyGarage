package controllers;

import dao.CarDao;
import dao.ServiceRecordDao;
import io.javalin.Javalin;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import services.ConvertToEntity;
import services.ImportVehicleService;
import services.VehicleApiReader;

import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.given;

class ServiceRecordControllerTest {

    private static Javalin app;

    @BeforeAll
    static void setUp() {
        app = Javalin.create();

        ServiceRecordDao serviceRecordDao = new ServiceRecordDao();
        ImportVehicleService importVehicleService =
                new ImportVehicleService(
                        new VehicleApiReader(),
                        new ConvertToEntity(),
                        new CarDao(),
                        serviceRecordDao
                );

        ServiceRecordController controller =
                new ServiceRecordController(serviceRecordDao, importVehicleService);

        controller.addRoutes(app);

        app.start(7070);
    }

    @AfterAll
    static void tearDown() {
        app.stop();
    }

    @Test
    void importVehicle_shouldReturn201() {

        given()
                .pathParam("registrationNumber", "EC74058")
                .when()
                .post("http://localhost:7070/service-records/import/{registrationNumber}")
                .then()
                .statusCode(201)
                .body("registrationNumber", is("EC74058"));
    }
}
