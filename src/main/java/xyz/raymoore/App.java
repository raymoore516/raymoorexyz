package xyz.raymoore;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import xyz.raymoore.connections.Connections;

public class App {
    public static int PORT = 8080;

    public static void main(String[] args) {
        // Initialize application with Javalin configuration
        Javalin app = Javalin.create(App::init);

        // TODO: Add pages after initial proof-of-concept
        app.get("/", ctx -> ctx.html("Hello world"));

        // Connections
        Connections connections = new Connections();
        app.get("connections", connections::home);
        app.post("connections", connections::submit);

        // Start application
        app.start(PORT);
    }

    private static void init(JavalinConfig config) {
        config.staticFiles.add("src/main/webapp", Location.EXTERNAL);
    }
}
