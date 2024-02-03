package com.application.poc.controller;

import com.application.poc.entity.PurchaseOrder;
import com.application.poc.model.OrderRequest;
import com.application.poc.model.OrderResponse;
import com.application.poc.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("order")
public class OrderController
{

    @Autowired
    private OrderService service;

    @PostMapping("/create")
    public Mono<PurchaseOrder> createOrder(@RequestBody Mono<OrderRequest> mono){
        return mono
                .flatMap(this.service::createOrder);
    }

    @GetMapping("/all")
    public Flux<OrderResponse> getOrders(){
        return this.service.getAll();
    }

}
