package security;

import com.fasterxml.jackson.databind.node.ObjectNode;
import dto.UserDTO;
import entities.User;
import io.javalin.http.Context;

public class SecurityController implements ISecurityController {

    private final ISecurityDAO securityDAO;

    public SecurityController(ISecurityDAO securityDAO) {
        this.securityDAO = securityDAO;
    }

    @Override
    public void login(Context ctx) {

        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        User user = securityDAO.getVerifiedUser(
                userDTO.getUsername(),
                userDTO.getPassword()
        );

        if (user == null) {
            ctx.status(401);
            ctx.json("Invalid username or password");
            return;
        }

        ctx.status(200);
        ctx.json("Login successful");
    }

    @Override
    public void register(Context ctx) {

        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        User user = securityDAO.createUser(
                userDTO.getUsername(),
                userDTO.getPassword()
        );

        ctx.status(201);
        ctx.json(user);
    }


    @Override
    public void authenticate(Context ctx) {
        // kommer i JWT-delen
    }

    @Override
    public void authorize(Context ctx) {
        // kommer i JWT-delen
    }
}