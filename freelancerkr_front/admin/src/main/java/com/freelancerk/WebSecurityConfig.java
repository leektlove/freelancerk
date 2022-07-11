package com.freelancerk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AdminCustomUserDetailsService adminCustomUserDetailsService;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        // Security configuration for H2 console access
        // !!!! You MUST NOT use this configuration for PRODUCTION site !!!!
        httpSecurity.authorizeRequests().antMatchers("/console/**").permitAll();
        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();

        // static resources
        httpSecurity.authorizeRequests()
                .antMatchers("/static/css/**", "/static/images/**", "/js/**", "/images/**", "/resources/**", "/webjars/**").permitAll();
//                .antMatchers("/**").permitAll();

        httpSecurity.authorizeRequests()
                .antMatchers("/login").anonymous()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureUrl("/login?error")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/", true)
                .and()
                .logout()
                .logoutSuccessUrl("/signin?logout");

        httpSecurity.exceptionHandling().accessDeniedPage("/");
        httpSecurity.sessionManagement().invalidSessionUrl("/login");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(adminCustomUserDetailsService);
        // In case of password encryption - for production site
        auth.userDetailsService(adminCustomUserDetailsService).passwordEncoder(passwordEncoder());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

