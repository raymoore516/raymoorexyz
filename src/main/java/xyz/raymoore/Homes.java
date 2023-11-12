package xyz.raymoore;

import net.jextra.fauxjo.HomeGroup;
import xyz.raymoore.db.Session;

public class Homes extends HomeGroup {
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
