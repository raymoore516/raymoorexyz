package xyz.raymoore;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.javalin.Javalin;
import io.javalin.config.JavalinConfig;
import io.javalin.http.staticfiles.Location;
import net.jextra.fauxjo.HomeGroup;
import xyz.raymoore.app.connections.Connections;
import xyz.raymoore.app.madisonsc.MadisonSC;
import xyz.raymoore.db.Session;
import xyz.raymoore.javalin.Filter;

import java.io.File;
import java.io.IOException;

public class App {
    public static String DEFAULT_PORT = "8080";

    public static ObjectMapper JACKSON_MAPPER = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .registerModule(new JavaTimeModule());

    public static void main(String[] args) throws IOException {
        // Load Settings
        File file = new File("src/env/settings.yaml");
        Settings settings = Settings.load(file);

        // Create Javalin
        Javalin app = Javalin.create(App::configure);

        // Before: Session Handler
        Filter filter = new Filter(settings);
        app.before(filter::before);

        // Endpoint: Home
        app.get("/", ctx -> ctx.html("Hello world"));

        // Endpoint: Connections
        Connections connections = new Connections(settings);
        connections.register(app);

        // Endpoint: Madison Super Contest
        MadisonSC madisonSC = new MadisonSC(settings);
        madisonSC.register(app);

        // Start Javalin
        int port = Integer.parseInt(settings.getPort() == null ? DEFAULT_PORT : settings.getPort());
        app.start(port);
    }

    // ---

    private static void configure(JavalinConfig config) {
        config.staticFiles.add("src/main/webapp", Location.EXTERNAL);
    }

    public static class Homes extends HomeGroup {
        private static Homes instance;

        public Homes() {
            addHome(Session.Home.class, new Session.Home());
        }

        public static synchronized Homes use() {
            if (instance == null) {
                instance = new Homes();
            }

            return instance;
        }

        public Session.Home getSessionHome() {
            return getHome(Session.Home.class);
        }
    }
}
