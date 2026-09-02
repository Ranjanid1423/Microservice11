package com.example.payment.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    /*
     * =========================================================
     * RETRY COUNTER
     * =========================================================
     *
     * Used for testing retry behavior.
     *
     * After reset:
     *
     * Attempt 1 -> HTTP 500
     * Attempt 2 -> HTTP 200
     * Attempt 3 -> HTTP 200
     * ...
     */

    private final AtomicInteger retryAttempt =
            new AtomicInteger(0);


    /*
     * =========================================================
     * HEALTH CHECK
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/health
     */

    @GetMapping("/health")
    public ResponseEntity<String> health() {

        return ResponseEntity.ok(
                "Payment Service is UP"
        );
    }


    /*
     * =========================================================
     * NORMAL PAYMENT
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/1
     */

    @GetMapping("/{paymentId}")
    public ResponseEntity<String> getPayment(
            @PathVariable Long paymentId) {

        return ResponseEntity.ok(
                "Payment "
                + paymentId
                + " processed successfully"
        );
    }


    /*
     * =========================================================
     * SLOW PAYMENT
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/slow/10
     *
     * The service waits for the requested number
     * of seconds before responding.
     */

    @GetMapping("/slow/{seconds}")
    public ResponseEntity<String> slowPayment(
            @PathVariable int seconds)
            throws InterruptedException {

        if (seconds < 0) {

            return ResponseEntity
                    .badRequest()
                    .body(
                            "Seconds cannot be negative"
                    );
        }

        System.out.println(
                "Payment Service: delaying for "
                + seconds
                + " seconds"
        );

        Thread.sleep(seconds * 1000L);

        System.out.println(
                "Payment Service: slow payment completed"
        );

        return ResponseEntity.ok(
                "Slow payment completed after "
                + seconds
                + " seconds"
        );
    }


    /*
     * =========================================================
     * RETRY TEST
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/retry-test
     *
     * Behavior:
     *
     * First request:
     *      HTTP 500
     *
     * Second request:
     *      HTTP 200
     *
     * Third and later:
     *      HTTP 200
     *
     * This is intentional.
     *
     * Order Service should retry after the first failure.
     */

    @GetMapping("/retry-test")
    public ResponseEntity<String> retryTest() {

        int attempt =
                retryAttempt.incrementAndGet();

        System.out.println(
                "Payment Service: retry-test attempt "
                + attempt
        );


        /*
         * First attempt deliberately fails.
         */

        if (attempt == 1) {

            System.out.println(
                    "Payment Service: deliberately "
                    + "returning HTTP 500"
            );

            return ResponseEntity
                    .internalServerError()
                    .body(
                            "Temporary Payment failure - "
                            + "attempt "
                            + attempt
                    );
        }


        /*
         * Second and later attempts succeed.
         */

        System.out.println(
                "Payment Service: payment recovered "
                + "on attempt "
                + attempt
        );

        return ResponseEntity.ok(
                "Payment recovered on attempt "
                + attempt
        );
    }


    /*
     * =========================================================
     * RESET RETRY COUNTER
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/retry-test/reset
     *
     * Resets the counter to zero.
     *
     * After reset:
     *
     * next retry-test request = attempt 1
     */

    @GetMapping("/retry-test/reset")
    public ResponseEntity<String> resetRetryTest() {

        retryAttempt.set(0);

        System.out.println(
                "Payment Service: retry counter reset"
        );

        return ResponseEntity.ok(
                "Retry test counter reset"
        );
    }


    /*
     * =========================================================
     * ALWAYS FAILING PAYMENT
     * =========================================================
     *
     * GET
     * http://localhost:8082/api/payments/fail
     *
     * Every request returns HTTP 500.
     *
     * Useful for testing what happens when
     * retries cannot recover the request.
     */

    @GetMapping("/fail")
    public ResponseEntity<String> failPayment() {

        System.out.println(
                "Payment Service: deliberate failure"
        );

        return ResponseEntity
                .internalServerError()
                .body(
                        "Payment Service deliberately failed"
                );
    }
}