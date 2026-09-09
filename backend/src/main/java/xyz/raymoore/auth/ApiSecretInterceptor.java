package xyz.raymoore.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tools.jackson.databind.ObjectMapper;

@Component
public class ApiSecretInterceptor implements HandlerInterceptor {

    private static final String API_SECRET_HEADER = "api-secret";

    private final String configuredSecret;
    private final ObjectMapper objectMapper;

    public ApiSecretInterceptor(
            @Value("${app.api-secret}") String configuredSecret,
            ObjectMapper objectMapper
    ) {
        this.configuredSecret = configuredSecret;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean preHandle(
            HttpServletRequest request,
            HttpServletResponse response,
            Object handler
    ) throws IOException {
        if (handler instanceof HandlerMethod handlerMethod
                && handlerMethod.hasMethodAnnotation(ApiSecretRequired.class)) {
            String suppliedSecret = request.getHeader(API_SECRET_HEADER);
            if (!configuredSecret.isBlank() && secretsMatch(configuredSecret, suppliedSecret)) {
                return true;
            }

            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid API secret"
            );
            problem.setTitle("Unauthorized");

            response.setStatus(problem.getStatus());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), problem);
            return false;
        } else {
            return true;
        }
    }

    private static boolean secretsMatch(String expected, String supplied) {
        if (supplied == null) {
            return false;
        }
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)
        );
    }
}
