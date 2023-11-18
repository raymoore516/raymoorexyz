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
}
