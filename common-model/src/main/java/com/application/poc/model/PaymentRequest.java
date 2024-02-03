package com.application.poc.model;

import lombok.Data;

import java.util.UUID;

@Data
public class PaymentRequest
{
    private Integer userId;
    private UUID orderId;
    private Double amount;
}
