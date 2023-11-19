package xyz.raymoore.app.connections.db;

import net.jextra.fauxjo.bean.FauxjoField;
import net.jextra.fauxjo.bean.FauxjoPrimaryKey;
import xyz.raymoore.app.connections.category.Color;

import java.util.UUID;

public class Group {
    @FauxjoPrimaryKey
    @FauxjoField("groupId")
    private UUID id;

    @FauxjoField("puzzleId")
    private UUID puzzleId;

    @FauxjoField("color")
    private Color color;

    @FauxjoField("description")
    private String description;

    @FauxjoField("words")
    private String[] words;

    // ---

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getPuzzleId() {
        return puzzleId;
    }

    public void setPuzzleId(UUID puzzleId) {
        this.puzzleId = puzzleId;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String[] getWords() {
        return words;
    }

    public void setWords(String[] words) {
        this.words = words;
    }

    // ---

    public static class Home extends net.jextra.fauxjo.Home<Group> {
        public static String SCHEMA = "connections";

        public Home() {
            super(String.format("%s.%s", SCHEMA, "Group"), Group.class);
        }
    }
}
