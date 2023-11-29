package xyz.raymoore.app.madisonsc.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class Contestant extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField(value = "contestantId", defaultable = true)
    private UUID id;

    @FauxjoField(value = "entryDate", defaultable = true)
    private Instant entryDate;

    @FauxjoField("name")
    private String name;

    // ---

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // ---

    public static class Home extends net.jextra.fauxjo.Home<Contestant> {
        public static final String SCHEMA = "madisonsc";

        public Home() {
            super(SCHEMA + ".Contestant", Contestant.class);
        }

        public Contestant find(UUID contestantId) throws SQLException {
            String clause = "WHERE contestantId=uuid(?)";
            PreparedStatement statement = prepareStatement(buildBasicSelect(clause));
            statement.setObject(1, contestantId);

            return getUnique(statement.executeQuery());
        }

        public List<Contestant> findAll() throws SQLException {
            String clause = "ORDER BY entryDate";
            PreparedStatement statement = prepareStatement(buildBasicSelect(clause));

            return getList(statement.executeQuery());
        }

        public Contestant findByName(String name) throws SQLException {
            String clause = "WHERE lower(name)=?";
            PreparedStatement statement = prepareStatement(buildBasicSelect(clause));
            statement.setString(1, name.toLowerCase());

            return getUnique(statement.executeQuery());
        }
    }
}
