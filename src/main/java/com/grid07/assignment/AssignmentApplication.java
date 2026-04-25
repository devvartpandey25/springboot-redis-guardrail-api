package com.grid07.assignment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;


@SpringBootApplication
@EnableScheduling // enables scheduled jobs (e.g. notification sweeper, cleanup tasks)
public class AssignmentApplication {

    public static void main(String[] args) {
        // Could add some startup logging here
        SpringApplication.run(AssignmentApplication.class, args);
    }
}