package uk.co.ticklethepanda.health.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

@Component
public class TokenAuthenticationService {

    private static class RoleJwtPayload {
        private String role;

        private RoleJwtPayload(String role) {
            this.role = role;
        }

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }
    }

    @Autowired
    private JwtTokenConfig jwtTokenConfig;

    private Gson gson = new GsonBuilder().create();

    public Authentication getAuthentication(HttpServletRequest servletRequest) {

        String header = servletRequest.getHeader("Authorization");

        if(header != null && header.startsWith("Bearer ")) {

            String authToken = header.replace("Bearer ", "");
            DecodedJWT jwt = jwtTokenConfig.getVerifier().verify(authToken);

            String payload = new String(Base64.getDecoder().decode(jwt.getPayload()));

            RoleJwtPayload jwtPayload = gson.fromJson(payload, RoleJwtPayload.class);

            return new UsernamePasswordAuthenticationToken(null, null, Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_" + jwtPayload.getRole())
            ));

        }

        return null;

    }

}
