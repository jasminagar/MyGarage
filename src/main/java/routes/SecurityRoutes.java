package routes;

import io.javalin.apibuilder.EndpointGroup;
import security.ISecurityController;

import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {

    private ISecurityController securityController;

    public SecurityRoutes(ISecurityController securityController) {
        this.securityController = securityController;
    }

    public EndpointGroup getSecurityRoutes() {
      return() ->{
          post("/auth/register", securityController::register);
          post("/auth/login", securityController::login);
      };
    }
}