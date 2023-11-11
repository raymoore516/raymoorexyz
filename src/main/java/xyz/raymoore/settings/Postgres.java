package xyz.raymoore.settings;

import org.postgresql.ds.PGSimpleDataSource;
import xyz.raymoore.Settings;

import javax.sql.DataSource;

public class Postgres extends Settings {
    private String user;
    private String password;
    private String server;
    private String database;

    // ---

    public DataSource createDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(read(user));
        ds.setPassword(read(password));
        ds.setServerNames(new String[]{read(server)});
        ds.setDatabaseName(read(database));

        return ds;
    }

    // ---

    public String getUser() {
        return read(user);
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return read(password);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServer() {
        return read(server);
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getDatabase() {
        return read(database);
    }

    public void setDatabase(String database) {
        this.database = database;
    }
}
