package com.leyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class LeyouCorsfiguration {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();

        corsConfiguration.addAllowedOrigin("http://manage.leyou.com");

        corsConfiguration.addAllowedMethod("POST");
        corsConfiguration.addAllowedMethod("OPTIONS");
        corsConfiguration.addAllowedMethod("DELETE");

        corsConfiguration.addAllowedHeader("*");

        UrlBasedCorsConfigurationSource urlconfig = new UrlBasedCorsConfigurationSource();

        urlconfig.registerCorsConfiguration("/**", corsConfiguration);

        return new CorsFilter(urlconfig);
    }
}
