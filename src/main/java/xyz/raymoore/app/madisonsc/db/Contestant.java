package xyz.raymoore.app.madisonsc.db;

import net.jextra.fauxjo.bean.Fauxjo;
import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;

import java.time.Instant;
import java.util.UUID;

public class Contestant extends Fauxjo {
    @FauxjoPrimaryKey
    @FauxjoField("contestantId")
    private UUID id;

    @FauxjoField("entryDate")
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
}
