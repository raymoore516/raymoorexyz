package xyz.raymoore;

import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;

public class App {
    public static int PORT = 8080;

    public static void main(String[] args) {
        // Initialize application with Javalin configuration
        Javalin app = Javalin.create(App::init);

        // TODO: Add pages after initial proof-of-concept
        app.get("/", ctx -> ctx.html("Hello world"));

        // Start application
        app.start(PORT);
    }

    private static void init(JavalinConfig config) {
        config.staticFiles.add("src/main/webapp", Location.EXTERNAL);
    }
}
