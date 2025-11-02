package com.joey.cheaterbuster.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Getter
@Configuration
public class LeetifyConfig {

    @Value("${leetify.api.base-url}")
    private String baseUrl;

    @Value("${leetify.api.key}")
    private String apiKey;

    @Value("${http.user-agent}")
    private String userAgent;

    @PostConstruct
    public void validateConfig() {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            log.error("LEETIFY_API_KEY environment variable is not set!");
            throw new IllegalStateException(
                "Leetify API key is required. Please set the LEETIFY_API_KEY environment variable."
            );
        }
        log.info("Leetify configuration validated successfully");
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
