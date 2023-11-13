package xyz.raymoore.javalin;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.AppHomes;
import xyz.raymoore.Settings;
import xyz.raymoore.db.Session;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class Filter {
    public static final String SESSION_COOKIE_KEY = "raymoore.xyz";

    private final DataSource ds;

    public Filter(Settings settings) {
        this.ds = settings.getPostgres().useDataSource();
    }

    public void before(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            AppHomes.use().setConnection(conn);
            handleSession(ctx);
        }
    }

    // ---

    private void handleSession(Context ctx) throws SQLException {
        // Handle scenario where browser cookie does not exist
        if (ctx.cookieStore().get(SESSION_COOKIE_KEY) == null) {
            createSessionCookie(ctx);
        }

        // Handle scenario where browser cookie is corrupt
        String cookie = ctx.cookieStore().get(SESSION_COOKIE_KEY);
        UUID sessionId = UUID.fromString(cookie);
        Session session = AppHomes.use().getSessionHome().find(sessionId);
        if (session == null) {
            createSessionCookie(ctx);
        }
    }

    private void createSessionCookie(Context ctx) throws SQLException {
        Session session = new Session(UUID.randomUUID());
        AppHomes.use().getSessionHome().insert(session);
        ctx.cookieStore().set(SESSION_COOKIE_KEY, session.getId().toString());
    }
}
