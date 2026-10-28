package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import app.security.SecurityController;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {
    private final SecurityController securityController;

    public SecurityRoutes(SecurityController securityController) {
        this.securityController = securityController;
    }

    public EndpointGroup getSecurityRoutes() {
      return() ->{
          post("/auth/register", securityController::register);
          post("/auth/login", securityController::login);
      };
    }
}