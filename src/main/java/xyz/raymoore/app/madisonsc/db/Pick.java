package xyz.raymoore.app.madisonsc.db;

import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;
import xyz.raymoore.app.madisonsc.category.Result;
import xyz.raymoore.app.madisonsc.category.Team;

import java.time.Instant;
import java.util.UUID;

public class Pick {
    @FauxjoPrimaryKey
    @FauxjoField("pickId")
    private UUID pickId;

    @FauxjoField("entryDate")
    private Instant entryDate;

    @FauxjoField("contestantId")
    private String contestantId;

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

    public UUID getPickId() {
        return pickId;
    }

    public void setPickId(UUID pickId) {
        this.pickId = pickId;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    public String getContestantId() {
        return contestantId;
    }

    public void setContestantId(String contestantId) {
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
}
