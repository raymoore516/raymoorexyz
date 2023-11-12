package xyz.raymoore.app.connections;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Homes;
import xyz.raymoore.Settings;
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

    public void render(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            Homes.use().setConnection(conn);

            String cookie = ctx.cookieStore().get(Filter.SESSION_COOKIE_KEY);
            UUID sessionId = UUID.fromString(cookie);
            Session session = Homes.use().getSessionHome().find(sessionId);

            Instant entryDate = session.getEntryDate();
            long seconds = Instant.now().getEpochSecond() - entryDate.getEpochSecond();

            ctx.html(String.format("Hello! You first visited this site %d seconds ago [sid: %s]", seconds, sessionId));
        }
    }

    public void submit(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            try (PreparedStatement statement = conn.prepareStatement("SELECT uuid_generate_v4()")) {
                try (ResultSet rs = statement.executeQuery()) {
                    rs.next();
                    ctx.json(String.format("{\"uuid\":\"%s\"}", rs.getString(1)));
                }
            }
        }
    }
}
