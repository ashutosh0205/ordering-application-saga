package com.application.poc.model;

import com.application.poc.enums.InventoryStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class InventoryResponse
{
    private UUID orderId;
    private Integer userId;
    private Integer productId;
    private InventoryStatus status;
}
