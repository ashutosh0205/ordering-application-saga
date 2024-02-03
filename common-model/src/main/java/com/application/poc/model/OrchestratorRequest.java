package com.application.poc.model;

import lombok.Data;

import java.util.UUID;

@Data
public class OrchestratorRequest
{

    private Integer userId;
    private Integer productId;
    private UUID orderId;
    private Double amount;


}