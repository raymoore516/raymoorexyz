package xyz.raymoore.app.madisonsc.api;

import java.util.List;

public class Submission {
    private String contestant;  // Matches on contestant name
    private List<String> picks;  // Uses shorthand notation

    // ---

    public String getContestant() {
        return contestant;
    }

    public void setContestant(String contestant) {
        this.contestant = contestant;
    }

    public List<String> getPicks() {
        return picks;
    }

    public void setPicks(List<String> picks) {
        this.picks = picks;
    }
}
