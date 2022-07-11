package com.freelancerk;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.thymeleaf.extras.java8time.dialect.Java8TimeDialect;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templateresolver.StringTemplateResolver;

@Configuration
public class ThymeleafConfiguration {

    @Bean(name ="stringTemplateEngine")
    public SpringTemplateEngine getTemplateEngine() {
        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
        templateEngine.setTemplateResolver(new StringTemplateResolver());
        return templateEngine;
    }

//    @Primary
//    @Bean(name ="defaultTemplateEngine")
//    public SpringTemplateEngine getDefaultTemplateEngine() {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//
//        templateEngine.setTemplateResolver(templateResolver());
//        templateEngine.addDialect(new SpringSecurityDialect());
//        templateEngine.addDialect(new LayoutDialect());
//        templateEngine.addDialect(new Java8TimeDialect());
//        return templateEngine;
//    }


    @Bean
    public ClassLoaderTemplateResolver templateResolver() {

        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();

        templateResolver.setPrefix("templates/");
        templateResolver.setCacheable(false);
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML5");
        templateResolver.setCharacterEncoding("UTF-8");

        return templateResolver;
    }
}
