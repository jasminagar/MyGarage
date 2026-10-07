package security;

import io.javalin.Javalin;

public class SecurityRoutes {

    private final ISecurityController securityController;

    public SecurityRoutes(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public void addRoutes(Javalin app) {
        app.post("/auth/register", securityController::register);
        app.post("/auth/login", securityController::login);
    }
}