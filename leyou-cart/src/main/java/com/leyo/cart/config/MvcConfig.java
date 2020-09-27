package com.leyo.cart.config;

import com.leyo.cart.interceptor.LoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableConfigurationProperties(Jwtproperties.class)
public class MvcConfig implements WebMvcConfigurer {

    @Autowired
    private Jwtproperties jwtproperties;

    @Bean
    public LoginInterceptor getLoginInterceptor() {
        return new LoginInterceptor(jwtproperties);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(getLoginInterceptor()).addPathPatterns("/**");
    }
}
