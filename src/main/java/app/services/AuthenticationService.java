package app.services;

import app.dto.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthenticationService {

    private final TokenService tokenService;

    public AuthenticationService(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    public void authenticate(Context ctx) {
        if (ctx.method().toString().equals("OPTIONS")) {
            ctx.status(200);
            return;
        }

        Set<String> allowedRoles = ctx.routeRoles().stream()
                .map(role -> role.toString().toUpperCase())
                .collect(Collectors.toSet());

        if (isOpenEndpoint(allowedRoles)) {
            return;
        }

        String token = getToken(ctx);
        UserDTO tokenUser = tokenService.verifyToken(token);
        ctx.attribute("user", tokenUser);
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        return allowedRoles.isEmpty()
                || allowedRoles.contains("ANYONE");
    }

    private static String getToken(Context context) {
        String header = context.header("Authorization");
        if (header == null) {
            throw new UnauthorizedResponse("Authorization header is missing"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }

        // If the Authorization Header was malformed, then no entry
        String token = header.split(" ")[1];
        if (token == null) {
            throw new UnauthorizedResponse("Authorization header is malformed"); // UnauthorizedResponse is javalin 6 specific but response is not json!
        }
        return token;
    }
}
