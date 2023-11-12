package xyz.raymoore;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import xyz.raymoore.settings.Postgres;

import java.io.File;
import java.io.IOException;

public class AppSettings {
    @JsonIgnore
    public static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());

    private String port;
    private Postgres postgres;
    private String secret;

    // ---

    public static AppSettings load(File yaml) throws IOException {
        return YAML_MAPPER.readValue(yaml, AppSettings.class);
    }

    public static String read(String value) {
        return value.startsWith("$") ? System.getenv(value.substring(1)) : value;
    }

    // ---

    public String getPort() {
        return read(port);
    }

    public void setPort(String port) {
        this.port = port;
    }

    public Postgres getPostgres() {
        return postgres;
    }

    public void setPostgres(Postgres postgres) {
        this.postgres = postgres;
    }

    public String getSecret() {
        return read(secret);
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }
}
