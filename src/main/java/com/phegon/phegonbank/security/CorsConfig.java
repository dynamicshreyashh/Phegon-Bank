package com.phegon.phegonbank.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.*;
import org.springframework.web.filter.CorsFilter;
import java.util.Arrays;

@Configuration
public class CorsConfig {
 @Bean
 public CorsFilter corsFilter(@Value("${APP_CORS_ORIGINS:http://localhost:5173}") String origins){
  CorsConfiguration config=new CorsConfiguration();
  Arrays.stream(origins.split(",")).map(String::trim).filter(s->!s.isBlank()).forEach(config::addAllowedOrigin);
  config.addAllowedHeader("*"); config.addAllowedMethod("*"); config.setMaxAge(3600L);
  UrlBasedCorsConfigurationSource source=new UrlBasedCorsConfigurationSource();source.registerCorsConfiguration("/**",config);return new CorsFilter(source);
 }
}
