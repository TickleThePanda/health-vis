package uk.co.ticklethepanda.activity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Lovingly hand crafted by the ISIS Business Applications Team
 */
@EnableAutoConfiguration
@EnableAsync
@EnableScheduling
@ComponentScan("uk.co.ticklethepanda.activity")
public class Driver {

    public static void main(String[] args) {
        SpringApplication.run(Driver.class);
    }
}
