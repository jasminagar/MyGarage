package security;

import io.javalin.security.RouteRole;

public enum EnumRoles implements RouteRole {
    USER,
    ADMIN
}
