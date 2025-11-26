package com.birtforge.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SampleApplication implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(SampleApplication.class);

    static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Sample Application started successfully!");
        logger.info("This is a simple console-based Spring Boot application.");
    }
}
