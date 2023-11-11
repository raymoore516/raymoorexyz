package xyz.raymoore;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import xyz.raymoore.connections.Connections;

import java.io.File;
import java.io.IOException;

public class App {
    public static int PORT = 8080;

    public static void main(String[] args) throws IOException {
        File file = new File("src/env/settings.yaml");
        Settings settings = Settings.from(file);

        Javalin app = Javalin.create(App::configure);
        app.get("/", ctx -> ctx.html("Hello world"));

        // Connections
        Connections connections = new Connections(settings);
        app.get("connections", connections::render);
        app.post("connections", connections::submit);

        // Start application
        app.start(PORT);
    }

    private static void configure(JavalinConfig config) {
        config.staticFiles.add("src/main/webapp", Location.EXTERNAL);
    }
}
