package com.leyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsConfig  {

    @Bean
    public CorsFilter getCorsFilter() {
        //添加cors配置信息
        CorsConfiguration cofig = new CorsConfiguration();
        //允许通过的域
        cofig.addAllowedOrigin("http://manage.leyou.com");
        cofig.addAllowedOrigin("http://www.leyou.com");
        //允许带cookie
        cofig.setAllowCredentials(true);

        //允许的请求
        cofig.addAllowedMethod("PUT");
        cofig.addAllowedMethod("GET");
        cofig.addAllowedMethod("POST");
        cofig.addAllowedMethod("DELETE");
        cofig.addAllowedMethod("HEAD");
        cofig.addAllowedMethod("OPTIONS");
        cofig.addAllowedMethod("PATCH");
        //允许的头信息
        cofig.addAllowedHeader("*");

        //拦截所有请求
        UrlBasedCorsConfigurationSource urlBasedCorsConfigurationSource = new UrlBasedCorsConfigurationSource();
        urlBasedCorsConfigurationSource.registerCorsConfiguration("/**", cofig);
        return new CorsFilter(urlBasedCorsConfigurationSource);

    }
}
