package ua.orlov.betreactive;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class BetReactiveApplication {

    public static void main(String[] args) {
        SpringApplication.run(BetReactiveApplication.class, args);
    }
}
