package co.inventorsoft.academy.spring.restfull.config;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        String errorMessage = getErrorMessage(authException);

        JsonObject errorResponse = new JsonObject();
        errorResponse.addProperty("error_code", "UNAUTHORIZED");
        errorResponse.addProperty("message", errorMessage);
        errorResponse.addProperty("timestamp", new Date().toString());

        response.getWriter().write(errorResponse.toString());
        response.getWriter().flush();
    }

    private static String getErrorMessage(AuthenticationException authException) {
        if (authException instanceof BadCredentialsException) {
            return "Invalid credentials";
        } else if (authException instanceof DisabledException) {
            return "Account is disabled";
        } else if (authException instanceof InsufficientAuthenticationException) { //user tries to access admin uri
            return "Insufficient authentication";
        } else if (authException instanceof UsernameNotFoundException) {
            return "Username not found";
        }

        return "Unauthorized";
    }
}
