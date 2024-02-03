package com.application.poc.model;

import lombok.Data;

import java.util.UUID;

@Data
public class InventoryRequest
{

    private Integer userId;
    private Integer productId;
    private UUID orderId;

}
