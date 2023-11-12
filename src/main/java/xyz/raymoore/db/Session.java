package xyz.raymoore.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.UUID;

public class Session extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField("sessionId")
    private UUID id;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Session() {
    }

    public Session(UUID id) {
        this.id = id;
    }

    public static class Home extends net.jextra.fauxjo.Home<Session> {
        public static final String SCHEMA = "app";

        public Home() {
            super(SCHEMA + ".Session", Session.class);
        }

        public Session find(UUID sessionId) throws SQLException {
            String clause = "where sessionId=uuid(?)";
            PreparedStatement statement = prepareStatement(buildBasicSelect(clause));
            statement.setObject(1, sessionId);

            return getUnique(statement.executeQuery());
        }
    }
}
