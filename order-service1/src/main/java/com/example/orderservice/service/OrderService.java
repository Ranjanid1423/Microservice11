package com.example.orderservice.service;

import org.springframework.stereotype.Service;

import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.model.Order;
import io.github.resilience4j.retry.annotation.Retry;

@Service
public class OrderService {

    private final PaymentClient paymentClient;

    public OrderService(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    // =========================================================
    // GET ORDER
    // =========================================================

    public Order getOrderById(Long orderId) {

        if (orderId == null) {
            return null;
        }

        // Test order
        if (orderId.equals(101L)) {

            Order order = new Order();

            order.setOrderId(101L);
            order.setUserId(1L);

            return order;
        }

        return null;
    }

    // =========================================================
    // GET USER
    // =========================================================

    public UserResponse getUserFromUserService(Long userId) {

        /*
         * Existing User Service test data.
         *
         * User Service:
         * ID    = 1
         * Name  = John
         * Email = john@example.com
         */

        if (userId != null && userId.equals(1L)) {

            return new UserResponse(
                    1L,
                    "John",
                    "john@example.com"
            );
        }

        return null;
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    public String createOrder(
            Long userId,
            Integer quantity,
            String productName) {

        return "Order created successfully for user "
                + userId
                + ", product "
                + productName
                + ", quantity "
                + quantity;
    }

    // =========================================================
    // NORMAL PAYMENT
    // =========================================================

    public String getPayment(Long paymentId) {

        return paymentClient.getPayment(paymentId);
    }

    // =========================================================
    // SLOW PAYMENT
    // =========================================================

    public String getSlowPayment(int seconds) {

        return paymentClient.getSlowPayment(seconds);
    }

    // =========================================================
    // RETRY PAYMENT
    // =========================================================

    @Retry(name = "paymentRetry")
    public String retryPayment() {

        return paymentClient.retryPayment();
    
    }

    // =========================================================
    // FAIL PAYMENT
    // =========================================================

    public String failPayment() {

        return paymentClient.failPayment();
    }

    // =========================================================
    // RESET RETRY TEST
    // =========================================================

    public String resetRetryTest() {

        return paymentClient.resetRetryTest();
    }

    // =========================================================
    // PAYMENT HEALTH
    // =========================================================

    public String healthCheck() {

        return paymentClient.healthCheck();
    }
}