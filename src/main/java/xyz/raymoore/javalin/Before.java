package xyz.raymoore.javalin;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Homes;
import xyz.raymoore.Settings;
import xyz.raymoore.db.Session;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class Before {
    public static final String SESSION_COOKIE_KEY = "raymoore.xyz";

    private final DataSource ds;

    public Before(Settings settings) {
        this.ds = settings.getPostgres().useDataSource();
    }

    public void session(@NotNull Context ctx) throws SQLException {
        if (ctx.cookieStore().get(SESSION_COOKIE_KEY) == null) {
            Session session = createSession();
            ctx.cookieStore().set(SESSION_COOKIE_KEY, session.getId());
        }
    }

    private Session createSession() throws SQLException {
        Session session = new Session(UUID.randomUUID());
        try (Connection conn = ds.getConnection()) {
            Homes.use().setConnection(conn);
            Homes.use().getSessionHome().insert(session);
            return session;
        }
    }
}
