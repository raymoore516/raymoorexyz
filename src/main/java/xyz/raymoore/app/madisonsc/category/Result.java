package xyz.raymoore.app.madisonsc.category;

import xyz.raymoore.app.madisonsc.db.Pick;

import java.util.List;

public enum Result {
    win("W"),
    loss("L"),
    tie("T");

    private final String code;

    Result(String code) {
        this.code = code;
    }

    public static Result findByCode(String code) {
        for (Result result : values()) {
            if (result.getCode().equals(code)) {
                return result;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public static String calculateRecord(List<Pick> picks) {
        int wins = 0, losses = 0, ties = 0;

        for (Pick pick : picks) {
            if (pick.getResult() == null) {
                continue;  // "TBD" game
            }

            switch (pick.getResult()) {
                case win -> wins++;
                case loss -> losses++;
                case tie -> ties++;
            }
        }

        return String.format("%s-%s-%s", wins, losses, ties);
    }
}
