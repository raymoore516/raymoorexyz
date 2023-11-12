package xyz.raymoore.app.madisonsc;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.javalin.Javalin;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Homes;
import xyz.raymoore.Settings;
import xyz.raymoore.app.madisonsc.bean.PicksMessage;
import xyz.raymoore.javalin.Routes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MadisonSC implements Routes {
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

    public void submitPicks(@NotNull Context ctx) throws SQLException, JsonProcessingException {
        try (Connection conn = ds.getConnection()) {
            Homes.use().setConnection(conn);

            int year = Integer.parseInt(ctx.pathParam("year"));
            int week = Integer.parseInt(ctx.pathParam("week"));
            PicksMessage message = ctx.bodyAsClass(PicksMessage.class);

            StorageEngine engine = new StorageEngine();
            engine.submitPicks(year, week, message);

            // TODO...
        }
    }
}
