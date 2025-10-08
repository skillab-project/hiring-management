package com.example.hiringProcess.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CORS πρέπει να είναι ενεργό για να σταλούν τα Access-Control-* headers
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                // H2 console σε iframe
                .headers(h -> h.frameOptions(f -> f.disable()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cors = new CorsConfiguration();
        // Από πού επιτρέπεις requests (React dev server)
        cors.setAllowedOriginPatterns(List.of("*"));
        // Ποιες μέθοδοι επιτρέπονται
        cors.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        // Headers που επιτρέπεις να σταλούν στο request
        cors.setAllowedHeaders(List.of("*"));
        // Headers που εκθέτεις στην απάντηση (αν χρειάζεται)
        cors.setExposedHeaders(List.of("*"));
        // Αν στέλνεις cookies/Authorization από browser
        cors.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Εφάρμοσε τα παραπάνω σε όλα τα endpoints
        source.registerCorsConfiguration("/**", cors);
        return source;
    }
}
