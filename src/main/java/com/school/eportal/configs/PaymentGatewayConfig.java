package com.school.eportal.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class PaymentGatewayConfig {

    @Bean
    public RestClient paystackRestClient(@Value("${paystack.secret.key}") String secret) {
        // Here you can configure timeouts, interceptors, and connection pooling
        return RestClient.builder()
                .baseUrl("https://api.paystack.co")
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + secret)
                .build();
    }

    private static String secretKey;

    @Value("${paystack.secret.key}")
    public void setSecretKey(String secretKey) {
        PaymentGatewayConfig.secretKey = secretKey;
    }

    public static String getSecretKey() {
        return secretKey;
    }
}