package xyz.raymoore.app.connections;

import io.javalin.Javalin;
import io.javalin.http.Context;
import net.jextra.fauxjo.HomeGroup;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.App;
import xyz.raymoore.Settings;
import xyz.raymoore.app.connections.db.Game;
import xyz.raymoore.app.connections.db.Group;
import xyz.raymoore.app.connections.db.Puzzle;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Connections {
    private final DataSource ds;
    private final String secret;

    // ---

    public Connections(Settings settings) {
        this.ds = settings.getPostgres().useDataSource();
        this.secret = settings.getSecret();
    }

    // ---

    public void register(Javalin app) {
        app.get("connections", this::showPage);
        app.post("connections", this::submitGuess);
    }

    // ---

    public void showPage(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            App.Homes.use().setConnection(conn);

            Homes homes = new Homes(conn);

            Puzzle puzzle = homes.getPuzzleHome().findMostRecent();
            List<Group> groups = homes.getGroupHome().findByPuzzle(puzzle);

            List<String> words = new ArrayList<>();
            groups.forEach(group -> words.addAll(List.of(group.getWords())));

            StringBuilder sb = new StringBuilder("The words are: ");
            for (int i = 0; i < words.size(); i++) {
                sb.append(i == 0 ? "" : ", ");
                sb.append(words.get(i));
            }

            ctx.html(sb.toString());
        }
    }

    public void submitGuess(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT uuid_generate_v4()")) {
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    ctx.json(String.format("{\"uuid\":\"%s\"}", rs.getString(1)));
                }
            }
        }
    }

    // ---

    public static class Homes extends HomeGroup {
        public Homes() {
            addHome(Game.Home.class, new Game.Home());
            addHome(Group.Home.class, new Group.Home());
            addHome(Puzzle.Home.class, new Puzzle.Home());
        }

        public Homes(Connection conn) throws SQLException {
            this();
            this.setConnection(conn);
        }

        public Game.Home getGameHome() {
            return getHome(Game.Home.class);
        }

        public Group.Home getGroupHome() {
            return getHome(Group.Home.class);
        }

        public Puzzle.Home getPuzzleHome() {
            return getHome(Puzzle.Home.class);
        }
    }

    // ---

    public static class Engine {
        private final Homes homes;

        public Engine(Connection conn) throws SQLException {
            this.homes = new Homes(conn);
        }
    }
}
