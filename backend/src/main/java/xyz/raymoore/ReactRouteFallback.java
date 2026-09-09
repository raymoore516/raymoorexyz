package xyz.raymoore;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
@ConditionalOnResource(resources = "classpath:/static/index.html")
public class ReactRouteFallback {

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleMissingResource(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) throws NoResourceFoundException {
        if (isPageNavigation(request)) {
            return "forward:/index.html";
        }

        // Let Spring's remaining exception resolvers handle ordinary missing resources.
        throw exception;
    }

    private static boolean isPageNavigation(HttpServletRequest request) {
        if (!HttpMethod.GET.matches(request.getMethod()) && !HttpMethod.HEAD.matches(request.getMethod())) {
            return false;
        }

        try {
            return MediaType.parseMediaTypes(Collections.list(request.getHeaders(HttpHeaders.ACCEPT))).stream()
                    .anyMatch(type -> type.equalsTypeAndSubtype(MediaType.TEXT_HTML) && type.getQualityValue() > 0);
        } catch (InvalidMediaTypeException exception) {
            return false;
        }
    }
}
