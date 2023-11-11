package xyz.raymoore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import xyz.raymoore.settings.Postgres;

import java.io.File;
import java.io.IOException;

public class Settings {
    @JsonIgnore
    public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private Postgres postgres;
    private String secret;

    // ---

    public static Settings from(File yaml) throws IOException {
        return YAML_MAPPER.readValue(yaml, Settings.class);
    }

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
