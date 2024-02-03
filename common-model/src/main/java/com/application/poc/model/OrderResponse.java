package com.application.poc.model;

import com.application.poc.enums.OrderStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class OrderResponse
{

    private UUID orderId;
    private Integer userId;
    private Integer productId;
    private Double amount;
    private OrderStatus status;

}
