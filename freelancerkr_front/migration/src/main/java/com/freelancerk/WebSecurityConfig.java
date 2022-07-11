package com.freelancerk;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();

        // static resources
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.GET, "**").permitAll()
                .antMatchers(HttpMethod.POST, "**").permitAll();


        httpSecurity.exceptionHandling().accessDeniedPage("/");
        httpSecurity.sessionManagement().invalidSessionUrl("/");

    }
}

