package xyz.raymoore.app.connections.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.UUID;

public class Puzzle extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField("puzzleId")
    private UUID id;

    @FauxjoField("puzzleDate")
    private LocalDate puzzleDate;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LocalDate getPuzzleDate() {
        return puzzleDate;
    }

    public void setPuzzleDate(LocalDate puzzleDate) {
        this.puzzleDate = puzzleDate;
    }

    // ---

    public static class Home extends net.jextra.fauxjo.Home<Puzzle> {
        public static String SCHEMA = "connections";

        public Home() {
            super(String.format("%s.%s", SCHEMA, "Puzzle"), Puzzle.class);
        }

        public Puzzle find(UUID id) throws SQLException {
            String sql = buildBasicSelect("where puzzleId=uuid(?)");
            PreparedStatement statement = prepareStatement(sql);
            statement.setObject(1, id);

            return getUnique(statement.executeQuery());
        }

        public Puzzle findMostRecent() throws SQLException {
            String sql = buildBasicSelect("order by puzzleDate desc limit 1");
            PreparedStatement statement = prepareStatement(sql);

            return getFirst(statement.executeQuery());
        }
    }
}
