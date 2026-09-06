package xyz.raymoore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class App {

    static void main(String[] args) {
        try (var context = SpringApplication.run(App.class, args)) {
            // This initial database smoke check exits after the startup runner finishes.
        }
    }

    @Bean
    CommandLineRunner listContestants(JdbcTemplate jdbc) {
        return args -> {
            var contestants = jdbc.queryForList("SELECT * FROM madisonsc.contestant");
            System.out.println("Found " + contestants.size() + " contestant(s).");
            contestants.forEach(System.out::println);
        };
    }
}
