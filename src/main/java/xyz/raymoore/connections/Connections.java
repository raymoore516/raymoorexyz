package xyz.raymoore.connections;

import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;

public class Connections {
    public void home(@NotNull Context ctx) {
        ctx.html("TODO: Add home page for Connections module");
    }

    public void submit(@NotNull Context ctx) {
        ctx.json("Ow.");
    }
}
