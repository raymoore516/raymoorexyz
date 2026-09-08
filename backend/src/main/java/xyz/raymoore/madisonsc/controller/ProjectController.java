package xyz.raymoore.madisonsc.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import xyz.raymoore.madisonsc.domain.Contestant;
import xyz.raymoore.madisonsc.dto.query.LatestWeekResponse;
import xyz.raymoore.madisonsc.dto.query.WeeklyPicksResponse;
import xyz.raymoore.madisonsc.repository.ContestantRepository;
import xyz.raymoore.madisonsc.service.PickQueryService;

@RestController
@RequestMapping("/api/madisonsc")
public class ProjectController {

    private final ContestantRepository contestantRepository;
    private final PickQueryService pickQueryService;

    public ProjectController(ContestantRepository contestantRepository, PickQueryService pickQueryService) {
        this.contestantRepository = contestantRepository;
        this.pickQueryService = pickQueryService;
    }

    @GetMapping("/contestants")
    public List<Contestant> getContestants() {
        return contestantRepository.findAllAlphabetically();
    }

    @GetMapping("/latest")
    public LatestWeekResponse getLatestWeek() {
        return pickQueryService.findLatestWeek();
    }

    @GetMapping("/picks/{year}/{week}")
    public WeeklyPicksResponse getWeeklyPicks(
            @PathVariable("year") int year,
            @PathVariable("week") int week
    ) {
        if (year < 1) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Year must be positive");
        }
        if (week < 1 || week > 18) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Week must be between 1 and 18");
        }
        return pickQueryService.findWeeklyPicks(year, week);
    }
}
