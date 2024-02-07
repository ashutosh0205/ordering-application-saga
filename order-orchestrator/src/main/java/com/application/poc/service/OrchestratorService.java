package com.application.poc.service;

import com.application.poc.model.InventoryRequest;
import com.application.poc.model.OrchestratorRequest;
import com.application.poc.model.OrchestratorResponse;
import com.application.poc.model.PaymentRequest;
import com.application.poc.enums.OrderStatus;
import com.application.poc.service.steps.InventoryStep;
import com.application.poc.service.steps.PaymentStep;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@Slf4j
public class OrchestratorService
{

    @Autowired
    @Qualifier("payment")
    private WebClient paymentClient;

    @Autowired
    @Qualifier("inventory")
    private WebClient inventoryClient;

    public Mono<OrchestratorResponse> orderProduct(final OrchestratorRequest requestDTO)
    {
        log.info("Processing order workflow for order: {}", requestDTO.getOrderId());
        Workflow orderWorkflow = this.getOrderWorkflow(requestDTO);
        return Flux.fromStream(() -> orderWorkflow.getSteps().stream()).flatMap(WorkflowStep::process)
                .handle(((aBoolean, synchronousSink) -> {
                    if (aBoolean)
                        synchronousSink.next(true);
                    else
                        synchronousSink.error(new WorkflowException("create order failed!"));
                })).then(Mono.fromCallable(() -> getResponseDTO(requestDTO, OrderStatus.ORDER_COMPLETED)))
                .onErrorResume(ex -> this.revertOrder(orderWorkflow, requestDTO));

    }

    private Mono<OrchestratorResponse> revertOrder(final Workflow workflow, final OrchestratorRequest requestDTO)
    {
        return Flux.fromStream(() -> workflow.getSteps().stream())
                .filter(wf -> wf.getStatus().equals(WorkflowStepStatus.COMPLETE)).flatMap(WorkflowStep::revert).retry(3)
                .then(Mono.just(this.getResponseDTO(requestDTO, OrderStatus.ORDER_CANCELLED)));
    }

    private Workflow getOrderWorkflow(OrchestratorRequest requestDTO)
    {
        WorkflowStep paymentStep = new PaymentStep(this.paymentClient, this.getPaymentRequestDTO(requestDTO));
        WorkflowStep inventoryStep = new InventoryStep(this.inventoryClient, this.getInventoryRequestDTO(requestDTO));
        return new OrderWorkflow(List.of(paymentStep, inventoryStep));
    }

    private OrchestratorResponse getResponseDTO(OrchestratorRequest requestDTO, OrderStatus status)
    {
        OrchestratorResponse responseDTO = new OrchestratorResponse();
        responseDTO.setOrderId(requestDTO.getOrderId());
        responseDTO.setAmount(requestDTO.getAmount());
        responseDTO.setProductId(requestDTO.getProductId());
        responseDTO.setUserId(requestDTO.getUserId());
        responseDTO.setStatus(status);
        return responseDTO;
    }

    private PaymentRequest getPaymentRequestDTO(OrchestratorRequest requestDTO)
    {
        PaymentRequest paymentRequestDTO = new PaymentRequest();
        paymentRequestDTO.setUserId(requestDTO.getUserId());
        paymentRequestDTO.setAmount(requestDTO.getAmount());
        paymentRequestDTO.setOrderId(requestDTO.getOrderId());
        return paymentRequestDTO;
    }

    private InventoryRequest getInventoryRequestDTO(OrchestratorRequest requestDTO)
    {
        InventoryRequest inventoryRequestDTO = new InventoryRequest();
        inventoryRequestDTO.setUserId(requestDTO.getUserId());
        inventoryRequestDTO.setProductId(requestDTO.getProductId());
        inventoryRequestDTO.setOrderId(requestDTO.getOrderId());
        return inventoryRequestDTO;
    }

}
