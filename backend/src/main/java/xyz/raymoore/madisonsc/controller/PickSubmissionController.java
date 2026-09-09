package xyz.raymoore.madisonsc.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import xyz.raymoore.auth.ApiSecretRequired;
import xyz.raymoore.madisonsc.dto.submission.PickSubmissionRequest;
import xyz.raymoore.madisonsc.dto.submission.PickSubmissionResponse;
import xyz.raymoore.madisonsc.service.PickSubmissionService;

@RestController
@RequestMapping("/api/madisonsc/picks")
public class PickSubmissionController {

    private final PickSubmissionService pickSubmissionService;

    public PickSubmissionController(PickSubmissionService pickSubmissionService) {
        this.pickSubmissionService = pickSubmissionService;
    }

    @ApiSecretRequired
    @PostMapping("/{year}/{week}")
    public PickSubmissionResponse submitWeeklyPicks(
            @PathVariable("year") int year,
            @PathVariable("week") int week,
            @Valid @RequestBody PickSubmissionRequest request
    ) {
        if (year < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year must be positive");
        }
        if (week < 1 || week > 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Week must be between 1 and 18");
        }

        pickSubmissionService.submitWeeklyPicks(year, week, request);
        return PickSubmissionResponse.builder().success(true).build();
    }
}
