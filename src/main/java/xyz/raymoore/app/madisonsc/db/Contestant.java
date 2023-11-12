package xyz.raymoore.app.madisonsc.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.time.Instant;
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
    }
}
