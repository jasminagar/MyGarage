package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import app.security.SecurityController;

import static io.javalin.apibuilder.ApiBuilder.*;

public class SecurityRoutes {
    private final SecurityController securityController;

    public SecurityRoutes(SecurityController securityController) {
        this.securityController = securityController;
    }

    public EndpointGroup getSecurityRoutes() {
        return () -> {
            path("/auth", () -> {
                get("/healthcheck", securityController::healthCheck);
                get("/", ctx -> {ctx.status(200);ctx.contentType("text/plain");ctx.result("Welcome to MyGarage API!");});
                post("/login", securityController::login);
                post("/register", securityController::register);
            });
        };

    }
}