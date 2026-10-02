package com.booking.hotel.admin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("admin-migrate")
public class AdminMigrationProcess implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminMigrationProcess.class);

    private final ConfigurableApplicationContext applicationContext;

    public AdminMigrationProcess(ConfigurableApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void run(ApplicationArguments args) {
        log.info("Liquibase migrations completed successfully. Stopping the administrative process.");
        SpringApplication.exit(applicationContext);
    }
}
