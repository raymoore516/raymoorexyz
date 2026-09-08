package xyz.raymoore.madisonsc.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import xyz.raymoore.madisonsc.category.Team;
import xyz.raymoore.madisonsc.domain.Contestant;
import xyz.raymoore.madisonsc.domain.Pick;
import xyz.raymoore.madisonsc.dto.PickSubmissionRequest;
import xyz.raymoore.madisonsc.repository.ContestantRepository;
import xyz.raymoore.madisonsc.repository.PickSubmissionRepository;

@Service
public class PickSubmissionService {

    private static final int MAX_PICKS_PER_WEEK = 5;
    private static final BigDecimal MAX_LINE = new BigDecimal("99.9");
    private static final Pattern PICK_SHORTHAND = Pattern.compile(
            "^(?<team>[A-Z]{2,3})\\s+(?<spread>PK|[+-]\\d{1,2}(?:\\.\\d)?)(?:\\s+(?<result>[WLT]))?$",
            Pattern.CASE_INSENSITIVE
    );
    private final ContestantRepository contestantRepository;
    private final PickSubmissionRepository pickSubmissionRepository;

    public PickSubmissionService(
            ContestantRepository contestantRepository,
            PickSubmissionRepository pickSubmissionRepository
    ) {
        this.contestantRepository = contestantRepository;
        this.pickSubmissionRepository = pickSubmissionRepository;
    }

    @Transactional
    public void submitWeeklyPicks(int year, int week, PickSubmissionRequest request) {
        String contestantName = request.contestant().trim();
        List<Contestant> contestants = contestantRepository.findByNameIgnoreCaseForUpdate(contestantName);

        if (contestants.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Contestant not found");
        }
        if (contestants.size() > 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Contestant name is ambiguous");
        }

        Contestant contestant = contestants.getFirst();
        long existingPickCount = pickSubmissionRepository.countForContestantWeek(
                contestant.contestantId(),
                year,
                week
        );
        if (existingPickCount + request.picks().size() > MAX_PICKS_PER_WEEK) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Submission would exceed five picks for this contestant, year, and week"
            );
        }

        List<Pick> picks = parsePicks(contestant.contestantId(), year, week, request.picks());
        Set<String> existingTeams = pickSubmissionRepository
                .findTeamsForContestantWeek(contestant.contestantId(), year, week)
                .stream()
                .map(team -> team.toUpperCase(Locale.ROOT))
                .collect(Collectors.toUnmodifiableSet());
        picks.stream()
                .map(Pick::team)
                .filter(existingTeams::contains)
                .findFirst()
                .ifPresent(team -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Team already submitted for this contestant, year, and week: " + team
                    );
                });
        pickSubmissionRepository.insertAll(picks);
    }

    private static List<Pick> parsePicks(
            UUID contestantId,
            int year,
            int week,
            List<String> shorthandPicks
    ) {
        if (shorthandPicks.isEmpty() || shorthandPicks.size() > MAX_PICKS_PER_WEEK) {
            throw badRequest("Between one and five picks are required");
        }

        List<Pick> picks = new ArrayList<>(shorthandPicks.size());
        Set<Team> selectedTeams = new HashSet<>();
        for (int index = 0; index < shorthandPicks.size(); index++) {
            ParsedPick parsedPick = parsePick(shorthandPicks.get(index), index + 1);
            if (!selectedTeams.add(parsedPick.team())) {
                throw badRequest("Duplicate team in picks: " + parsedPick.team());
            }
            picks.add(parsedPick.toPick(contestantId, year, week));
        }
        return List.copyOf(picks);
    }

    private static ParsedPick parsePick(String shorthand, int pickNumber) {
        Matcher matcher = PICK_SHORTHAND.matcher(shorthand.trim());
        if (!matcher.matches()) {
            throw badRequest(
                    "Pick " + pickNumber + " must use TEAM +/-SPREAD [W|L|T] or TEAM PK [W|L|T]"
            );
        }

        Team team = parseTeam(matcher.group("team"), pickNumber);

        String spread = matcher.group("spread").toUpperCase(Locale.ROOT);
        BigDecimal line = spread.equals("PK")
                ? BigDecimal.ZERO
                : new BigDecimal(spread.substring(1));
        if (line.compareTo(MAX_LINE) > 0) {
            throw badRequest("Spread in pick " + pickNumber + " cannot exceed 99.9");
        }

        return new ParsedPick(
                team,
                line.signum() == 0 ? false : spread.startsWith("+"),
                line,
                parseResult(matcher.group("result"))
        );
    }

    private static Team parseTeam(String teamCode, int pickNumber) {
        String normalizedCode = teamCode.toUpperCase(Locale.ROOT);
        try {
            return Team.valueOf(normalizedCode);
        } catch (IllegalArgumentException exception) {
            throw badRequest("Unknown team in pick " + pickNumber + ": " + normalizedCode);
        }
    }

    private static String parseResult(String resultCode) {
        if (resultCode == null) {
            return null;
        }
        return switch (resultCode.toUpperCase(Locale.ROOT)) {
            case "W" -> "win";
            case "L" -> "loss";
            case "T" -> "tie";
            default -> throw new IllegalStateException("Unexpected validated result code: " + resultCode);
        };
    }

    private static ResponseStatusException badRequest(String reason) {
        return new ResponseStatusException(HttpStatus.BAD_REQUEST, reason);
    }

    private record ParsedPick(Team team, Boolean underdog, BigDecimal line, String result) {

        Pick toPick(UUID contestantId, int year, int week) {
            return Pick.builder()
                    .contestantId(contestantId)
                    .year(year)
                    .week(week)
                    .team(team.name())
                    .underdog(underdog)
                    .line(line)
                    .result(result)
                    .build();
        }
    }
}
