package xyz.raymoore.madisonsc.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.raymoore.madisonsc.domain.Contestant;
import xyz.raymoore.madisonsc.repository.ContestantRepository;

@RestController
@RequestMapping("/api/madisonsc")
public class HomeController {

    private final ContestantRepository contestantRepository;

    public HomeController(ContestantRepository contestantRepository) {
        this.contestantRepository = contestantRepository;
    }

    @GetMapping("/contestants")
    public List<Contestant> getContestants() {
        return contestantRepository.findAllAlphabetically();
    }
}
