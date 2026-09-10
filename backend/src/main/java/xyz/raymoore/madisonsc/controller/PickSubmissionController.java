package xyz.raymoore.madisonsc.controller;

import jakarta.validation.Valid;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import xyz.raymoore.madisonsc.dto.submission.PickSubmissionRequest;
import xyz.raymoore.madisonsc.dto.submission.PickSubmissionResponse;
import xyz.raymoore.madisonsc.service.PickSubmissionService;

@RestController
@RequestMapping("/api/madisonsc/picks")
public class PickSubmissionController {

    private static final String API_SECRET_HEADER = "api-secret";

    private final String configuredSecret;
    private final PickSubmissionService pickSubmissionService;

    public PickSubmissionController(
            @Value("${app.api-secret}") String configuredSecret,
            PickSubmissionService pickSubmissionService
    ) {
        this.configuredSecret = configuredSecret;
        this.pickSubmissionService = pickSubmissionService;
    }

    @PostMapping("/{year}/{week}")
    public PickSubmissionResponse submitWeeklyPicks(
            @RequestHeader(API_SECRET_HEADER) String suppliedSecret,
            @PathVariable("year") int year,
            @PathVariable("week") int week,
            @Valid @RequestBody PickSubmissionRequest request
    ) {
        // Validate secret
        if (configuredSecret.isBlank()) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "API secret not configured");
        } else if (!secretsMatch(configuredSecret, suppliedSecret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid API secret");
        }

        // Validate path parameters
        if (year < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year must be positive");
        }
        if (week < 1 || week > 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Week must be between 1 and 18");
        }

        pickSubmissionService.submitWeeklyPicks(year, week, request);
        return PickSubmissionResponse.builder().success(true).build();
    }

    private static boolean secretsMatch(String expected, String supplied) {
        return MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                supplied.getBytes(StandardCharsets.UTF_8)
        );
    }
}
