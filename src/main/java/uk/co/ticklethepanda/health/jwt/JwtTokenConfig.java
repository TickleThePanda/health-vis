package uk.co.ticklethepanda.health.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

@Component
public class JwtTokenConfig {

    private final JWTVerifier verifier;
    private final String jwtSecret;

    @Autowired
    public JwtTokenConfig(
            @Value("${jwt.secret.key}") String jwtSecret) throws UnsupportedEncodingException {
        this.jwtSecret = jwtSecret;

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);

        this.verifier = JWT.require(algorithm)
                .build();
    }

    public String getJwtSecret() {
        return jwtSecret;
    }

    public JWTVerifier getVerifier() {
        return verifier;
    }
}
