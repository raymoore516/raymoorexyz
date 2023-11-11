package xyz.raymoore.connections;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Settings;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Connections {
    private final DataSource ds;
    private final String secret;

    public Connections(Settings settings) {
        this.ds = settings.getPostgres().createDataSource();
        this.secret = settings.getSecret();
    }

    public void render(@NotNull Context ctx) {
        ctx.html(String.format("The secret is: %s", secret));
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
