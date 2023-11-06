package xyz.raymoore;

import xyz.raymoore.db.Postgres;

public class Settings {
    private Postgres postgres;
    private String secret;

    // ---

    public Postgres getPostgres() {
        return postgres;
    }

    public void setPostgres(Postgres postgres) {
        this.postgres = postgres;
    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
