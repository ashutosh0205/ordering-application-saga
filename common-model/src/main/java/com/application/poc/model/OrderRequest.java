package com.application.poc.model;

import lombok.Data;

import java.util.UUID;

@Data
public class OrderRequest
{

    private Integer userId;
    private Integer productId;
    private UUID orderId;

}