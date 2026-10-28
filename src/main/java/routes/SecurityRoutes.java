package routes;

import io.javalin.apibuilder.EndpointGroup;
import security.SecurityController;
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