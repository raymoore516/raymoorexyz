package xyz.raymoore.app.madisonsc.db;

import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;
import xyz.raymoore.app.madisonsc.category.Result;
import xyz.raymoore.app.madisonsc.category.Team;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Pick {
    @FauxjoPrimaryKey
    @FauxjoField(value = "pickId", defaultable = true)
    private UUID id;

    @FauxjoField(value = "entryDate", defaultable = true)
    private Instant entryDate;

    @FauxjoField("contestantId")
    private UUID contestantId;

    @FauxjoField("year")
    private int year;

    @FauxjoField("week")
    private int week;

    @FauxjoField("team")
    private Team team;

    @FauxjoField("favorite")
    private boolean favorite;

    @FauxjoField("line")
    private double line;

    @FauxjoField("result")
    private Result result;

    // ---

    @Override
    public String toString() {
        return String.format("%s [year: %d] [week: %d] [team: %s] [id: %s]",
                this.getClass().getSimpleName(), year, week, team, id);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    public UUID getContestantId() {
        return contestantId;
    }

    public void setContestantId(UUID contestantId) {
        this.contestantId = contestantId;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public double getLine() {
        return line;
    }

    public void setLine(double line) {
        this.line = line;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    // ---

    public static class Home extends net.jextra.fauxjo.Home<Pick> {
        public static final String SCHEMA = "madisonsc";

        public Home() {
            super(SCHEMA + ".Pick", Pick.class);
        }

        public List<Pick> findByYearAndWeek(int year, int week) throws SQLException {
            String clause = "where year=? and week=? order by entryDate";
            PreparedStatement statement = prepareStatement(buildBasicSelect(clause));
            statement.setInt(1, year);
            statement.setInt(2, week);

            return getList(statement.executeQuery());
        }
    }
}
