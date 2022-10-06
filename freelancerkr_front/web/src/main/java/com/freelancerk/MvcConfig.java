package com.freelancerk;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.freelancerk.interceptor.AccessRecordInterceptor;
import com.freelancerk.interceptor.CommonVariableInterceptor;
import com.freelancerk.interceptor.MenuHitInterceptor;

@Configuration
public class MvcConfig extends WebMvcConfigurerAdapter {

    private AccessRecordInterceptor accessRecordInterceptor;

    @Autowired
    public MvcConfig(AccessRecordInterceptor accessRecordInterceptor) {
        this.accessRecordInterceptor = accessRecordInterceptor;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/webjars/**").
                addResourceLocations("classpath:/META-INF/resources/webjars/");
        registry.addResourceHandler("/static/**").
                addResourceLocations("classpath:/static/");
    }

    @Bean
    public CommonVariableInterceptor userInterceptor() {
        return new CommonVariableInterceptor();
    }

    @Bean
    public MenuHitInterceptor menuHitInterceptor() {
        return new MenuHitInterceptor();
    }

    public @Override void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(menuHitInterceptor())
                .addPathPatterns("/**");

        registry.addInterceptor(accessRecordInterceptor)
                .addPathPatterns("/**");
    }


}
