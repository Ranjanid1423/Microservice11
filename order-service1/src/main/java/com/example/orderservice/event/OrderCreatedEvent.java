package com.example.orderservice.event;

public class OrderCreatedEvent {

    private String eventType;

    private Long orderId;

    private Long userId;

    private String productName;

    private Integer quantity;


    // Default constructor
    public OrderCreatedEvent() {
    }


    // Parameterized constructor
    public OrderCreatedEvent(
            String eventType,
            Long orderId,
            Long userId,
            String productName,
            Integer quantity) {

        this.eventType = eventType;
        this.orderId = orderId;
        this.userId = userId;
        this.productName = productName;
        this.quantity = quantity;
    }


    // Getter and Setter for eventType
    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }


    // Getter and Setter for orderId
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }


    // Getter and Setter for userId
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    // Getter and Setter for productName
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }


    // Getter and Setter for quantity
    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}