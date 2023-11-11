package xyz.raymoore.settings;

import com.fasterxml.jackson.annotation.JsonGetter;
import org.postgresql.ds.PGSimpleDataSource;
import xyz.raymoore.Settings;

import javax.sql.DataSource;

public class Postgres {
    private String user;
    private String password;
    private String server;
    private String database;

    // ---

    public DataSource createDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(Settings.get(user));
        ds.setPassword(Settings.get(password));
        ds.setServerNames(new String[]{ Settings.get(server) });
        ds.setDatabaseName(Settings.get(database));

        return ds;
    }

    // ---

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    @JsonGetter
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
