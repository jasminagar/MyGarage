package app.security;

import app.exceptions.UserAlreadyExistsException;
import app.services.TokenService;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import app.dao.UserDao;
import app.dto.UserDTO;
import app.entities.User;
import io.javalin.http.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

public class SecurityController {

    private static final Logger logger = LoggerFactory.getLogger(SecurityController.class);
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserDao userDao;
    private final TokenService tokenService;

    public SecurityController(UserDao userDao, TokenService tokenService) {
        this.userDao = userDao;
        this.tokenService = tokenService;
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

            String token = tokenService.createTokenValues(tokenUser);

            context.status(HttpStatus.OK);
            context.json(returnObject
                    .put("token", token)
                    .put("username", tokenUser.getUsername()));

        } catch (UnauthorizedResponse e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void register(Context ctx) {
        UserDTO userDTO;
        try {
            userDTO = ctx.bodyAsClass(UserDTO.class);
        } catch (Exception e) {
            logger.warn("Registration failed: invalid request body");
            throw new BadRequestResponse(
                    "Invalid request body"
            );
        }

        try {
            User user = userDao.createUser(
                    userDTO.getUsername(),
                    userDTO.getPassword()
            );

            ctx.status(HttpStatus.CREATED);
            ctx.json(objectMapper.createObjectNode()
                    .put("username", user.getUsername()));

        } catch (UserAlreadyExistsException e) {
            logger.warn("Username already exists");
            throw new ConflictResponse("Username already exists");

        } catch (Exception e) {
            logger.error("Registration failed", e);
            throw e;
        }
    }

    public void healthCheck(@NotNull Context ctx) {
        ctx.status(200).json("{\"msg\": \"API is up and running\"}");
    }
}