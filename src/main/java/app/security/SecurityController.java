package app.security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import app.dao.UserDao;
import app.dto.UserDTO;
import app.entities.User;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController {
    private final TokenSecurity tokenSecurity = new TokenSecurity();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDao userDao;

    public SecurityController(UserDao userDao) {
        this.userDao = userDao;
    }

    public void login(Context context) {
        ObjectNode returnObject = objectMapper.createObjectNode();

        try {
            UserDTO userDTO = context.bodyAsClass(UserDTO.class);
            User verifiedUser = userDao.getVerifiedUser(
                    userDTO.getUsername(),
                    userDTO.getPassword()
            );

            if (verifiedUser == null) {
                throw new UnauthorizedResponse("Invalid username or password");
            }

            UserDTO tokenUser = new UserDTO(
                    verifiedUser.getUsername(),
                    verifiedUser.getRolesAsStrings()
            );
            String token = createToken(tokenUser);
            context.status(HttpStatus.OK);
            context.json(returnObject
                    .put("token", token)
                    .put("username", tokenUser.getUsername()));
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String createToken(UserDTO user) {
        String issuer = getSetting("ISSUER");
        String expirationTime = getSetting("TOKEN_EXPIRE_TIME");
        String secret = getSetting("SECRET_KEY");

        return tokenSecurity.createToken(
                user,
                issuer,
                expirationTime,
                secret
        );
    }

    public void register(Context ctx) {

        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        User user = userDao.createUser(
                userDTO.getUsername(),
                userDTO.getPassword()
        );

        ctx.status(HttpStatus.CREATED);
        ctx.json(objectMapper.createObjectNode().put("username", user.getUsername()));
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
        UserDTO tokenUser = verifyToken(token);
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

    private UserDTO verifyToken(String token) {
        String secret = getSetting("SECRET_KEY");

        try {
            if (!tokenSecurity.tokenIsValid(token, secret)
                    || !tokenSecurity.tokenNotExpired(token)) {
                throw new UnauthorizedResponse("Invalid or expired token");
            }

            return tokenSecurity.getUserWithRolesFromToken(token);

        } catch (JWTVerificationException e) {
            throw new UnauthorizedResponse("Could not verify token");
        }
    }

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

    private String getSetting(String key) {
        if (System.getenv("DEPLOYED") != null) {
            String value = System.getenv(key);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "Missing environment variable: " + key
                );
            }

            return value;
        }

        try (InputStream input = getClass().getClassLoader()
                .getResourceAsStream("config.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "app.config.properties was not found"
                );
            }

            Properties properties = new Properties();
            properties.load(input);

            String value = properties.getProperty(key);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "Missing setting in app.config.properties: " + key
                );
            }

            return value;

        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                    "Could not read app.config.properties", e
            );
        }
    }
}