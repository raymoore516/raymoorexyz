package xyz.raymoore.settings;

import org.postgresql.ds.PGSimpleDataSource;
import xyz.raymoore.Settings;

import javax.sql.DataSource;
import java.util.Set;

public class Postgres {
    private String user;
    private String password;
    private String server;
    private String database;

    // ---

    public DataSource createDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(Settings.read(user));
        ds.setPassword(Settings.read(password));
        ds.setServerNames(new String[]{Settings.read(server)});
        ds.setDatabaseName(Settings.read(database));

        return ds;
    }

    // ---

    public String getUser() {
        return Settings.read(user);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return Settings.read(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return Settings.read(server);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return Settings.read(database);
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
