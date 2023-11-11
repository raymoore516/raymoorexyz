package xyz.raymoore;

import xyz.raymoore.settings.Postgres;

public class Settings {
    private Postgres postgres;
    private String secret;

    // ---

    public static String get(String value) {
        return value.startsWith("$") ? System.getenv(value.substring(1)) : value;
    }

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
