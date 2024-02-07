package com.application.poc.service;

import java.util.Map;

import javax.annotation.PostConstruct;

import com.application.poc.entity.PurchaseOrder;
import com.application.poc.model.OrchestratorRequest;
import com.application.poc.model.OrchestratorResponse;
import com.application.poc.model.OrderRequest;
import com.application.poc.model.OrderResponse;
import com.application.poc.enums.OrderStatus;
import com.application.poc.repository.PurchaseOrderRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Service
@Slf4j
public class OrderService
{

    // product price map
    private static final Map<Integer, Double> PRODUCT_PRICE = Map.of(1, 100d, 2, 200d, 3, 300d);

    @Autowired
    private PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    private OrchestratorService orchestratorService;

    @Autowired
    private Sinks.Many<OrchestratorRequest> sink;

    @PostConstruct
    public void subscribe()
    {
        sink.asFlux().flatMap(dto -> this.orchestratorService.orderProduct(dto))
                .doOnNext(c -> log.info("Consuming :: {}", c))
                .doOnNext(dto -> log.info("Status : {}", dto.getStatus()))
                .flatMap(responseDTO -> this.updateOrder(responseDTO))
                .subscribe();

    }

    public Mono<PurchaseOrder> createOrder(OrderRequest orderRequestDTO)
    {
        return this.purchaseOrderRepository.save(this.dtoToEntity(orderRequestDTO))
                .doOnNext(e -> orderRequestDTO.setOrderId(e.getId())).doOnNext(e -> this.emitEvent(orderRequestDTO));
    }

    public Flux<OrderResponse> getAll()
    {
        return this.purchaseOrderRepository.findAll().map(this::entityToDto);
    }

    private void emitEvent(OrderRequest orderRequestDTO)
    {
        this.sink.tryEmitNext(this.getOrchestratorRequestDTO(orderRequestDTO));
    }

    public Mono<Void> updateOrder(final OrchestratorResponse responseDTO)
    {
        return this.purchaseOrderRepository.findById(responseDTO.getOrderId())
                .doOnNext(p -> p.setStatus(responseDTO.getStatus())).flatMap(this.purchaseOrderRepository::save).then();
    }

    private PurchaseOrder dtoToEntity(final OrderRequest dto)
    {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(dto.getOrderId());
        purchaseOrder.setProductId(dto.getProductId());
        purchaseOrder.setUserId(dto.getUserId());
        purchaseOrder.setStatus(OrderStatus.ORDER_CREATED);
        purchaseOrder.setPrice(PRODUCT_PRICE.get(purchaseOrder.getProductId()));
        return purchaseOrder;
    }

    private OrderResponse entityToDto(final PurchaseOrder purchaseOrder)
    {
        OrderResponse dto = new OrderResponse();
        dto.setOrderId(purchaseOrder.getId());
        dto.setProductId(purchaseOrder.getProductId());
        dto.setUserId(purchaseOrder.getUserId());
        dto.setStatus(purchaseOrder.getStatus());
        dto.setAmount(purchaseOrder.getPrice());
        return dto;
    }

    public OrchestratorRequest getOrchestratorRequestDTO(OrderRequest orderRequestDTO)
    {
        OrchestratorRequest requestDTO = new OrchestratorRequest();
        requestDTO.setUserId(orderRequestDTO.getUserId());
        requestDTO.setAmount(PRODUCT_PRICE.get(orderRequestDTO.getProductId()));
        requestDTO.setOrderId(orderRequestDTO.getOrderId());
        requestDTO.setProductId(orderRequestDTO.getProductId());
        return requestDTO;
    }

}
