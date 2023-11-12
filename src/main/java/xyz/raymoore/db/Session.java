package xyz.raymoore.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

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
            super(SCHEMA + ".session", Session.class);
        }
    }
}
