package xyz.raymoore;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import xyz.raymoore.app.connections.Connections;
import xyz.raymoore.javalin.Before;

import java.io.File;
import java.io.IOException;

public class App {
    public static String DEFAULT_PORT = "8080";

    public static void main(String[] args) throws IOException {
        // Settings File
        File file = new File("src/env/settings.yaml");
        Settings settings = Settings.load(file);

        // Javalin App + Home Page
        Javalin app = Javalin.create(App::configure);

        // Before: Session Handler
        Before before = new Before(settings);
        app.before(before::handle);

        // Endpoint: Home
        app.get("/", ctx -> ctx.html("Hello world"));

        // Endpoint: Connections
        Connections connections = new Connections(settings);
        app.get("connections", connections::render);
        app.post("connections", connections::submit);

        int port = Integer.parseInt(settings.getPort() == null ? DEFAULT_PORT : settings.getPort());
        app.start(port);
    }

    private static void configure(JavalinConfig config) {
        config.staticFiles.add("src/main/webapp", Location.EXTERNAL);
    }
}
