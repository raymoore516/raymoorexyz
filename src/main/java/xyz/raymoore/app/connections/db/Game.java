package xyz.raymoore.app.connections.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.time.Instant;
import java.util.UUID;

public class Game extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField("gameId")
    private UUID gameId;

    @FauxjoField("sessionId")
    private UUID sessionId;

    @FauxjoField("entryDate")
    private Instant entryDate;

    // ---

    public UUID getGameId() {
        return gameId;
    }

    public void setGameId(UUID gameId) {
        this.gameId = gameId;
    }

    public UUID getSessionId() {
        return sessionId;
    }

    public void setSessionId(UUID sessionId) {
        this.sessionId = sessionId;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }
}
