package com.topviec.topviec_be.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {

    private static final List<String> LOCAL_ALLOWED_ORIGINS = Arrays.asList(
            "http://localhost:3000",
            "http://localhost:4200",
            "http://localhost:4173",
            "http://localhost:5173");

    private final Environment environment;

    public CorsConfig(Environment environment) {
        this.environment = environment;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(resolveAllowedOrigins());
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept", "x-no-retry"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private List<String> resolveAllowedOrigins() {
        List<String> allowedOrigins = new ArrayList<>(LOCAL_ALLOWED_ORIGINS);
        String configuredOrigins = environment.getProperty("CORS_ALLOWED_ORIGINS");

        if (StringUtils.hasText(configuredOrigins)) {
            Arrays.stream(configuredOrigins.split(","))
                    .map(String::trim)
                    .filter(StringUtils::hasText)
                    .forEach(origin -> addOriginIfAbsent(allowedOrigins, origin));
            return allowedOrigins;
        }

        addOriginIfAbsent(allowedOrigins, environment.getProperty("APP_BASE_URL"));
        return allowedOrigins;
    }

    private void addOriginIfAbsent(List<String> allowedOrigins, String origin) {
        if (StringUtils.hasText(origin) && !allowedOrigins.contains(origin.trim())) {
            allowedOrigins.add(origin.trim());
        }
    }
}
