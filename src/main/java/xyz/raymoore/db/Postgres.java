package xyz.raymoore.db;

import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;

public class Postgres {
    private String user;
    private String password;
    private String server;
    private String database;

    // ---

    public DataSource toDataSource() {
        PGSimpleDataSource ds = new PGSimpleDataSource();
        ds.setUser(user);
        ds.setPassword(password);
        ds.setServerNames(new String[]{server});
        ds.setDatabaseName(database);

        return ds;
    }

    // ---

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

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
