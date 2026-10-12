package security;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dto.UserDTO;
import entities.User;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.HttpStatus;
import io.javalin.http.UnauthorizedResponse;
import jakarta.persistence.EntityManagerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController implements ISecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    private final ISecurityDAO securityDAO;
    private final TokenSecurity tokenSecurity = new TokenSecurity();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public SecurityController(ISecurityDAO securityDAO) {
        this.securityDAO = securityDAO;
    }

    @Override
    public void login(Context context) {
        //Tomt json objekt ved hjælp af obejectmapper.
        //Jeg bruger den til at samle de oplysninger jeg vil sende tilbage til klienten.
        //ObjectNode er et objekt der repræsenterer json data som jeg kan tilføje felter til.
        //objectmapper er Jackson værktøj til at arbejde med json.
        ObjectNode returnObject = objectMapper.createObjectNode();//For sending json object back to client.
        try {
            //Modtager data fra http request body, og konverter json data til java objekt.
            //Jeg bruger javalin context.bodyAsClass, så jeg senere kan hente værdier med getter.
            UserDTO userDTO = context.bodyAsClass(UserDTO.class);

            //Database user
            User verifiedUser = securityDAO.getVerifiedUser(
                    userDTO.getUsername(),
                    userDTO.getPassword()
            );

            if (verifiedUser == null) {
                throw new UnauthorizedResponse("Invalid username or password");
            }

            // Convert the database user to a DTO containing username and roles
            UserDTO tokenUser = new UserDTO(
                    verifiedUser.getUsername(),
                    verifiedUser.getRolesAsStrings()
            );

            System.out.println("tokenUser is null: " + (tokenUser == null));
            System.out.println("username: " + tokenUser.getUsername());
            System.out.println("roles: " + tokenUser.getRoles());

            //Kræver userDTO, derfor convert.
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

    @Override
    public void register(Context ctx) {

        UserDTO userDTO = ctx.bodyAsClass(UserDTO.class);

        User user = securityDAO.createUser(
                userDTO.getUsername(),
                userDTO.getPassword()
        );

        ctx.status(HttpStatus.CREATED);
        // Return only public information, not the password hash
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


        // Routes without roles, or with ANYONE, are public
        if (isOpenEndpoint(allowedRoles)) {
            return;
        }

        String token = getToken(ctx);
        UserDTO tokenUser = verifyToken(token);

        System.out.println("Verified user: " + tokenUser.getUsername());
        ctx.attribute("user", tokenUser);

        System.out.println("Verified user: " + tokenUser.getUsername());


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
                throw new ForbiddenResponse(
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

    // Read settings from environment variables when deployed,
    // otherwise from config.properties
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
                        "config.properties was not found"
                );
            }

            Properties properties = new Properties();
            properties.load(input);

            String value = properties.getProperty(key);

            if (value == null || value.isBlank()) {
                throw new IllegalStateException(
                        "Missing setting in config.properties: " + key
                );
            }

            return value;

        } catch (java.io.IOException e) {
            throw new IllegalStateException(
                    "Could not read config.properties", e
            );
        }
    }
}