package app.services;

import app.dto.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

import java.util.Set;
import java.util.stream.Collectors;

public class AuthorizationService {

    public void authorize(Context ctx) {
        Set<String> allowedRoles = ctx.routeRoles()
                .stream()
                .map(role -> role.toString().toUpperCase())
                .collect(Collectors.toSet());

        if (isOpenEndpoint(allowedRoles)) {
            return;
        }

        UserDTO user = ctx.attribute("user");
        if (user == null) {
            throw new UnauthorizedResponse(
                    "No user was added from the token");
        }

        if (!hasAllowedRole(user, allowedRoles)){
            throw new ForbiddenResponse(
                    "User was not authorized with roles: " + user.getRoles()
                            + ". Needed roles are: " + allowedRoles);
        }
    }

    private boolean hasAllowedRole(UserDTO user, Set<String> allowedRoles) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return false;
        }

        return user.getRoles().stream()
                .map(Enum::name)
                .anyMatch(allowedRoles::contains);
    }

    private boolean isOpenEndpoint(Set<String> allowedRoles) {
        return allowedRoles.isEmpty()
                || allowedRoles.contains("ANYONE");
    }
}
