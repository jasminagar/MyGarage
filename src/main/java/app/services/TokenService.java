package app.services;

import app.config.AppConfig;
import app.dto.UserDTO;
import app.security.EnumRoles;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import io.javalin.http.UnauthorizedResponse;

import java.time.Instant;
import java.util.List;
import java.util.Set;

public class TokenService {

    private final AppConfig appConfig;

    public TokenService(AppConfig appConfig) {
        this.appConfig = appConfig;
    }

    public String createTokenValues(UserDTO user) {
        String issuer = appConfig.getSetting("ISSUER");
        String expirationTime = appConfig.getSetting("TOKEN_EXPIRE_TIME");
        String secret = appConfig.getSetting("SECRET_KEY");

        return createToken(
                user,
                issuer,
                expirationTime,
                secret
        );
    }

    private String  createToken(UserDTO userDTO, String issuer, String expTime, String secret){
        long expirationMillis = Long.parseLong(expTime);
        if (expirationMillis <= 0 || issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("Issuer and positive expiration are required");
        }

        Instant now = Instant.now();

        String[] roles = userDTO.getRoles().stream()
                .map(Enum::name)
                .toArray(String[]::new);

        return JWT.create()
                .withIssuer(issuer)
                .withSubject(userDTO.getUsername())
                .withArrayClaim("roles", roles)
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(expirationMillis))
                .sign(signingAlgorithm(secret));
    }

    public UserDTO verifyToken(String token) {
        String secret = appConfig.getSetting("SECRET_KEY");

        try {
            if (!tokenIsValid(token, secret)
                    || !tokenNotExpired(token)) {
                throw new UnauthorizedResponse("Invalid or expired token");
            }

            return getUserWithRolesFromToken(token);

        } catch (JWTVerificationException e) {
            throw new UnauthorizedResponse("Could not verify token");
        }
    }

    /** Verifies the signature, expiry, and required identity claims. */
    public boolean tokenIsValid(String token, String secret){
        Algorithm algorithm = signingAlgorithm(secret);

        try{
            var jwt = JWT.require(algorithm)
                    .withClaimPresence("sub")
                    .withClaimPresence("roles")
                    .withClaimPresence("exp")
                    .build()
                    .verify(token);

            if (jwt.getExpiresAtAsInstant() == null) {
                return false;
            }

            getUserWithRolesFromToken(token);
            return  true;
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    /** Checks expiry only; this does not verify the signature. */
    public boolean tokenNotExpired(String token) {
        try {
            Instant expiration = JWT.decode(token).getExpiresAtAsInstant();
            return expiration != null && Instant.now().isBefore(expiration);
        } catch (JWTVerificationException e) {
            return false;
        }
    }

    public UserDTO getUserWithRolesFromToken(String token) {
        var jwt = JWT.decode(token);
        String username = jwt.getSubject();
        List<String> roleNames = jwt.getClaim("roles").asList(String.class);

        if (username == null || username.isBlank() || roleNames == null) {
            throw new JWTVerificationException("Token must contain a username and roles");
        }

        try {
            Set<EnumRoles> roles = roleNames.stream()
                    .map(EnumRoles::valueOf)
                    .collect(java.util.stream.Collectors.toSet());

            return new UserDTO(username, roles);

        } catch (IllegalArgumentException e) {
            throw new JWTVerificationException("Token contains an invalid role");
        }
    }

    private Algorithm signingAlgorithm(String secret) {
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("SECRET_KEY must be configured");
        }

        return Algorithm.HMAC256(secret);
    }
}
