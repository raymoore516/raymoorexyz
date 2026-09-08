package xyz.raymoore.madisonsc.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import xyz.raymoore.madisonsc.domain.Contestant;
import xyz.raymoore.madisonsc.repository.ContestantRepository;

@org.springframework.web.bind.annotation.RestController
@RequestMapping("/api/madisonsc")
public class RestController {

    private final ContestantRepository contestantRepository;

    public RestController(ContestantRepository contestantRepository) {
        this.contestantRepository = contestantRepository;
    }

    @GetMapping("/contestants")
    public List<Contestant> getContestants() {
        return contestantRepository.findAllAlphabetically();
    }
}
