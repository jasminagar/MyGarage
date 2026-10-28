package app.services;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

class ImportVehicleTest {

    @BeforeAll
    static void setUp() {
        RestAssured.baseURI = "http://localhost:7070";
    }

    @Test
    void importVehicleReturns401WithoutToken() {
        given()
                .contentType("application/json")
                .when()
                .post("/api/v1/service-records/import/CR48908")
                .then()
                .statusCode(401);
    }

    @Test
    void importVehicleReturns201WhenSuccessful() {
        String token = given()
                .contentType("application/json")
                .body("""
            {
                "username": "bob80",
                "password": "1234"
            }
            """)
                .when()
                .post("/api/v1/auth/login")
                .then()
                .statusCode(200)
                .extract()
                .path("token");

        given()
                .header("Authorization", "Bearer " + token)
                .when()
                .post("/api/v1/service-records/import/CR48908")
                .then()
                .statusCode(201);
    }
}