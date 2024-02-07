package com.application.poc.service.steps;

import com.application.poc.model.InventoryRequest;
import com.application.poc.model.InventoryResponse;
import com.application.poc.enums.InventoryStatus;
import com.application.poc.service.WorkflowStep;
import com.application.poc.service.WorkflowStepStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Slf4j
public class InventoryStep implements WorkflowStep {

    private final WebClient webClient;
    private final InventoryRequest requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    public InventoryStep(WebClient webClient, InventoryRequest requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        log.info("performing inventory step for order: {}", this.requestDTO.getOrderId());
        return this.webClient
                .post()
                .uri("/inventory/deduct")
                .body(BodyInserters.fromValue(this.requestDTO))
                .retrieve()
                .bodyToMono(InventoryResponse.class)
                .map(r -> r.getStatus().equals(InventoryStatus.AVAILABLE))
                .doOnNext(b -> {
                    WorkflowStepStatus status = this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED;
                    log.info("Inventory Step Status: {}", status);
                });
    }

    @Override
    public Mono<Boolean> revert() {
        log.info("Reverting inventory step for order: {}", this.requestDTO.getOrderId());
        return this.webClient
                    .post()
                    .uri("/inventory/add")
                    .body(BodyInserters.fromValue(this.requestDTO))
                    .retrieve()
                    .bodyToMono(Void.class)
                    .map(r ->true)
                    .onErrorReturn(false);
    }
}
