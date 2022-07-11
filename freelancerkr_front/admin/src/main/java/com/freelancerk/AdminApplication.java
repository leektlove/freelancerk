package com.freelancerk;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EntityScan(basePackageClasses = {AdminApplication.class, Jsr310JpaConverters.class} )
public class AdminApplication {

    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(AdminApplication.class, args);
    }
}
