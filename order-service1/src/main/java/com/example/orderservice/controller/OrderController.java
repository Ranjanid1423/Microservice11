package com.example.orderservice.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.orderservice.dto.CreateOrderRequest;
import com.example.orderservice.dto.UserResponse;
import com.example.orderservice.model.Order;
import com.example.orderservice.service.OrderService;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // =========================================================
    // GET ORDER
    // =========================================================

    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(
            @PathVariable Long orderId) {

        Order order = orderService.getOrderById(orderId);

        if (order == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(order);
    }

    // =========================================================
    // GET USER
    // =========================================================

    @GetMapping("/user/{userId}")
    public ResponseEntity<UserResponse> getUser(
            @PathVariable Long userId) {

        UserResponse user =
                orderService.getUserFromUserService(userId);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(user);
    }

    // =========================================================
    // CREATE ORDER
    // =========================================================

    @PostMapping
    public ResponseEntity<String> createOrder(
            @Valid @RequestBody CreateOrderRequest request) {

        String response = orderService.createOrder(
                request.getUserId(),
                request.getQuantity(),
                request.getProductName()
        );

        return ResponseEntity.ok(response);
    }

    // =========================================================
    // NORMAL PAYMENT
    // =========================================================

    @GetMapping("/payment/{paymentId}")
    public ResponseEntity<String> getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                orderService.getPayment(paymentId)
        );
    }

    // =========================================================
    // SLOW PAYMENT
    // =========================================================

    @GetMapping("/slow-payment/{seconds}")
    public ResponseEntity<String> getSlowPayment(
            @PathVariable int seconds) {

        return ResponseEntity.ok(
                orderService.getSlowPayment(seconds)
        );
    }

    // =========================================================
    // RETRY PAYMENT
    // =========================================================

    @GetMapping("/retry-payment")
    public ResponseEntity<String> retryPayment() {

        return ResponseEntity.ok(
                orderService.retryPayment()
        );
    }

    // =========================================================
    // FAIL PAYMENT
    // =========================================================

    @GetMapping("/fail-payment")
    public ResponseEntity<String> failPayment() {

        return ResponseEntity.ok(
                orderService.failPayment()
        );
    }

    // =========================================================
    // RESET RETRY TEST
    // =========================================================

    @GetMapping("/reset-retry")
    public ResponseEntity<String> resetRetryTest() {

        return ResponseEntity.ok(
                orderService.resetRetryTest()
        );
    }

    // =========================================================
    // PAYMENT HEALTH
    // =========================================================

    @GetMapping("/payment-health")
    public ResponseEntity<String> healthCheck() {

        return ResponseEntity.ok(
                orderService.healthCheck()
        );
    }
}