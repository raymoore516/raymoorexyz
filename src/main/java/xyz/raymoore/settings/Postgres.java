package xyz.raymoore.settings;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.postgresql.ds.PGSimpleDataSource;
import xyz.raymoore.AppSettings;

import javax.sql.DataSource;

public class Postgres {
    private String user;
    private String password;
    private String server;
    private String database;

    @JsonIgnore
    private PGSimpleDataSource ds;

    // ---

    public DataSource useDataSource() {
        if (ds == null) {
            ds = new PGSimpleDataSource();
            ds.setUser(AppSettings.read(user));
            ds.setPassword(AppSettings.read(password));
            ds.setServerNames(new String[]{AppSettings.read(server)});
            ds.setDatabaseName(AppSettings.read(database));
        }

        return ds;
    }

    // ---

    public String getUser() {
        return AppSettings.read(user);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return AppSettings.read(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return AppSettings.read(server);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return AppSettings.read(database);
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
