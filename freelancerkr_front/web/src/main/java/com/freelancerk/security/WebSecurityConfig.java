package com.freelancerk.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
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
    private Environment environment;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    private CustomAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf().disable();
        httpSecurity.headers().frameOptions().disable();

        // static resources
        httpSecurity.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/freelancer/profile/**").permitAll();
//                .antMatchers(HttpMethod.POST, "/users", "/certifications/confirm", "/users/temp-password", "/test/email").permitAll();

        httpSecurity
        	.authorizeRequests()
        		.antMatchers("/resources/**").permitAll()
        		.antMatchers("/public/**").permitAll()
        		.antMatchers("/view/pick-me-ups").permitAll()
        		.antMatchers("/robots.txt").permitAll()
//        		.antMatchers("/client/**").hasRole("CLIENT")
//        		.antMatchers("/freelancer/**").hasRole("FREELANCER")
        		.antMatchers("/view/**").authenticated()
        		.antMatchers("/auth/loginClient").permitAll()
        		.antMatchers("/auth/loginFreelancer", "/join/**", "/login/**", "/main/**", "/users/email-for-resetting-password", "/users/password/modifications", "/test/**", "/redirect/projects/**", "/redirect/contests/**").permitAll()
        		.antMatchers("/views/temp-payment", "/confirm/temp-purchases").permitAll()
        		.antMatchers("/").permitAll()
                .and()
                .addFilterAt(new LoginProcessingFilter(environment, "/login", customUserDetailsService, authenticationManager()), UsernamePasswordAuthenticationFilter.class)

            .formLogin()
            	.loginProcessingUrl("/login")
                .loginPage("/auth/login")
                .failureUrl("/auth/login")
                .defaultSuccessUrl("/client/profile")
                .usernameParameter("username")
                .passwordParameter("password")
                .and()
            .logout()
                .logoutSuccessUrl("/");

        httpSecurity.exceptionHandling().accessDeniedPage("/");
        httpSecurity.sessionManagement().invalidSessionUrl("/");

    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
        auth.authenticationProvider(authenticationProvider);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
}

