package com.freelancerk;

import com.freelancerk.controller.MigrationController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.ApplicationContext;

@SpringBootApplication(exclude = {HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class})
//@EntityScan(basePackageClasses = {Api.class, Jsr310JpaConverters.class} )
public class MigrationApplication {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(MigrationApplication.class, args);
        MigrationController migrationController = ctx.getBean(MigrationController.class);
        migrationController.triggerMigration();
    }
}
