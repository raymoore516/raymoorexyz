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
import xyz.raymoore.db.Session;
import xyz.raymoore.javalin.Filter;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.util.UUID;

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

            String cookie = ctx.cookieStore().get(Filter.SESSION_COOKIE_KEY);
            UUID sessionId = UUID.fromString(cookie);
            Session session = App.Homes.use().getSessionHome().find(sessionId);

            Instant entryDate = session.getEntryDate();
            long seconds = Instant.now().getEpochSecond() - entryDate.getEpochSecond();

            ctx.html(String.format("Hello! You first visited this site %d seconds ago [sid: %s]", seconds, sessionId));
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
