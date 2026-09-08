package xyz.raymoore.madisonsc.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

@Component
public class PickSubmissionFilter extends OncePerRequestFilter {

    private static final Pattern SUBMISSION_PATH = Pattern.compile("^/api/madisonsc/picks/[^/]+/[^/]+$");
    private static final String API_SECRET_HEADER = "api-secret";

    private final String configuredSecret;
    private final ObjectMapper objectMapper;

    public PickSubmissionFilter(
            @Value("${app.api-secret}") String configuredSecret,
            ObjectMapper objectMapper
    ) {
        this.configuredSecret = configuredSecret;
        this.objectMapper = objectMapper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !HttpMethod.POST.matches(request.getMethod())
                || !SUBMISSION_PATH.matcher(request.getServletPath()).matches();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        String suppliedSecret = request.getHeader(API_SECRET_HEADER);
        if (configuredSecret.isBlank() || !secretsMatch(configuredSecret, suppliedSecret)) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid API secret"
            );
            problem.setTitle("Unauthorized");

            response.setStatus(problem.getStatus());
            response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(), problem);
            return;
        }

        filterChain.doFilter(request, response);
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
