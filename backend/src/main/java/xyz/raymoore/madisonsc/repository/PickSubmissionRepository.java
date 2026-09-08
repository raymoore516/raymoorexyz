package xyz.raymoore.madisonsc.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import xyz.raymoore.madisonsc.domain.Pick;

@Repository
public class PickSubmissionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PickSubmissionRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public long countForContestantWeek(UUID contestantId, int year, int week) {
        Long count = jdbcTemplate.queryForObject("""
                SELECT COUNT(*)
                FROM madisonsc.pick
                WHERE contestant_id = :contestantId
                  AND year = :year
                  AND week = :week
                """, new MapSqlParameterSource()
                .addValue("contestantId", contestantId)
                .addValue("year", year)
                .addValue("week", week), Long.class);
        return count == null ? 0 : count;
    }

    public List<String> findTeamsForContestantWeek(UUID contestantId, int year, int week) {
        return jdbcTemplate.queryForList("""
                SELECT team
                FROM madisonsc.pick
                WHERE contestant_id = :contestantId
                  AND year = :year
                  AND week = :week
                """, new MapSqlParameterSource()
                .addValue("contestantId", contestantId)
                .addValue("year", year)
                .addValue("week", week), String.class);
    }

    public void insertAll(List<Pick> picks) {
        MapSqlParameterSource[] parameters = picks.stream()
                .map(pick -> new MapSqlParameterSource()
                        .addValue("contestantId", pick.contestantId())
                        .addValue("year", pick.year())
                        .addValue("week", pick.week())
                        .addValue("team", pick.team())
                        .addValue("underdog", pick.underdog())
                        .addValue("line", pick.line())
                        .addValue("result", pick.result()))
                .toArray(MapSqlParameterSource[]::new);

        jdbcTemplate.batchUpdate("""
                INSERT INTO madisonsc.pick (
                    contestant_id, year, week, team, underdog, line, result
                ) VALUES (
                    :contestantId, :year, :week, :team, :underdog, :line, :result
                )
                """, parameters);
    }
}
