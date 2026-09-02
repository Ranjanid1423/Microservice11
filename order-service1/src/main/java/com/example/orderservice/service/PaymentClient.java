package com.example.orderservice.service;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class PaymentClient {

    private final RestClient restClient;

    public PaymentClient(RestClient.Builder restClientBuilder) {

        this.restClient = restClientBuilder
                .baseUrl("http://localhost:8082")
                .build();
    }

    // =========================================================
    // NORMAL PAYMENT
    // =========================================================

    public String getPayment(Long paymentId) {

        return restClient.get()
                .uri("/api/payments/{paymentId}", paymentId)
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // SLOW PAYMENT
    // =========================================================

    public String getSlowPayment(int seconds) {

        return restClient.get()
                .uri("/api/payments/slow/{seconds}", seconds)
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // RETRY PAYMENT
    // =========================================================

    public String retryPayment() {

        return restClient.get()
                .uri("/api/payments/retry-test")
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // FAIL PAYMENT
    // =========================================================

    public String failPayment() {

        return restClient.get()
                .uri("/api/payments/fail")
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // RESET RETRY TEST
    // =========================================================

    public String resetRetryTest() {

        return restClient.get()
                .uri("/api/payments/retry-test/reset")
                .retrieve()
                .body(String.class);
    }

    // =========================================================
    // PAYMENT HEALTH
    // =========================================================

    public String healthCheck() {

        return restClient.get()
                .uri("/api/payments/health")
                .retrieve()
                .body(String.class);
    }
}