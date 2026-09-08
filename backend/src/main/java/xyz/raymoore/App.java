package xyz.raymoore;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import xyz.raymoore.madisonsc.repository.ContestantRepository;

@SpringBootApplication
public class App {

    static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    CommandLineRunner listContestants(ContestantRepository repository) {
        return args -> {
            var contestants = repository.findAllAlphabetically();
            System.out.println("Found " + contestants.size() + " contestant(s).");
            contestants.forEach(System.out::println);
        };
    }
}
