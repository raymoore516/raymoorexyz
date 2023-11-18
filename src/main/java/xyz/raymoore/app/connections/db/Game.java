package xyz.raymoore.app.connections.db;

import com.fasterxml.jackson.core.JsonProcessingException;
import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;
import xyz.raymoore.App;

import java.io.IOException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

public class Game extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField("gameId")
    private UUID gameId;

    @FauxjoField("sessionId")
    private UUID sessionId;

    @FauxjoField("puzzleId")
    private UUID puzzleId;

    @FauxjoField("data")
    private String dataEncoded;  // base64 encoded game state information

    @FauxjoField("entryDate")
    private Instant entryDate;

    // ---

    public Data getData() throws IOException {
        if (dataEncoded == null) {
            return null;
        }

        byte[] bytes = Base64.getDecoder().decode(dataEncoded);
        return App.JACKSON_MAPPER.readValue(bytes, Data.class);
    }

    public void setData(Data data) throws JsonProcessingException {
        if (data == null) {
            this.dataEncoded = null;
        }

        byte[] bytes = App.JACKSON_MAPPER.writeValueAsBytes(data);
        this.dataEncoded = Base64.getEncoder().encodeToString(bytes);
    }

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

    public UUID getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(UUID puzzleId) {
        this.puzzleId = puzzleId;
    }

    public String getDataEncoded() {
        return dataEncoded;
    }

    public void setDataEncoded(String dataEncoded) {
        this.dataEncoded = dataEncoded;
    }

    public Instant getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(Instant entryDate) {
        this.entryDate = entryDate;
    }

    // ---

    public static class Data {
        private List<Guess> guesses;

        public List<Guess> getGuesses() {
            return guesses;
        }

        public void setGuesses(List<Guess> guesses) {
            this.guesses = guesses;
        }
    }

    public static class Guess {
        private String[] words;
        private UUID groupId;
        private boolean oneAway;

        public String[] getWords() {
            return words;
        }

        public void setWords(String[] words) {
            this.words = words;
        }

        public UUID getGroupId() {
            return groupId;
        }

        public void setGroupId(UUID groupId) {
            this.groupId = groupId;
        }

        public boolean isOneAway() {
            return oneAway;
        }

        public void setOneAway(boolean oneAway) {
            this.oneAway = oneAway;
        }
    }
}
