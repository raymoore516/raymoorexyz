package xyz.raymoore;

import net.jextra.fauxjo.HomeGroup;
import xyz.raymoore.db.Session;

public class AppHomes extends HomeGroup {
    private static AppHomes instance;

    public AppHomes() {
        addHome(Session.Home.class, new Session.Home());
    }

    public static synchronized AppHomes use() {
        if (instance == null) {
            instance = new AppHomes();
        }

        return instance;
    }

    public Session.Home getSessionHome() {
        return getHome(Session.Home.class);
    }
}
