package xyz.raymoore;

import net.jextra.fauxjo.HomeGroup;
import xyz.raymoore.db.Session;
import xyz.raymoore.javalin.Routes;

import java.util.Map;

public class Homes extends HomeGroup {
    private static Homes instance;
    private static Map<Class<? extends Routes>, Homes> instances;

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
