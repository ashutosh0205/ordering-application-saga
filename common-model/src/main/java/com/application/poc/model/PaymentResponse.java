package com.application.poc.model;

import com.application.poc.enums.PaymentStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class PaymentResponse
{
    private Integer userId;
    private UUID orderId;
    private Double amount;
    private PaymentStatus status;
}
