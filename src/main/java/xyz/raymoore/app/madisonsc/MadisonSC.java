package xyz.raymoore.app.madisonsc;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Homes;
import xyz.raymoore.Settings;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MadisonSC {
    private final DataSource ds;

    // ---

    public MadisonSC(Settings settings) {
        this.ds = settings.getPostgres().useDataSource();
    }

    // ---

    public void register(Javalin app) {
        app.get("madisonsc/{year}/{week}", this::showPage);
        app.post("madisonsc/{year}/{week}", this::submitPicks);
    }

    public void showPage(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            Homes.use().setConnection(conn);

            ctx.html(String.format("The year is %s and the week is %s",
                            ctx.pathParam("year"), ctx.pathParam("week")));
        }
    }

    public void submitPicks(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            Homes.use().setConnection(conn);

            // TODO...
        }
    }
}
