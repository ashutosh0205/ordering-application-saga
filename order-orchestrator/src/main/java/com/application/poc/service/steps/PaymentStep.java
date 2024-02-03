package com.application.poc.service.steps;

import com.application.poc.model.PaymentRequest;
import com.application.poc.model.PaymentResponse;
import com.application.poc.enums.PaymentStatus;
import com.application.poc.service.WorkflowStep;
import com.application.poc.service.WorkflowStepStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
@Slf4j
public class PaymentStep implements WorkflowStep {

    private final WebClient webClient;
    private final PaymentRequest requestDTO;
    private WorkflowStepStatus stepStatus = WorkflowStepStatus.PENDING;

    public PaymentStep(WebClient webClient, PaymentRequest requestDTO) {
        this.webClient = webClient;
        this.requestDTO = requestDTO;
    }

    @Override
    public WorkflowStepStatus getStatus() {
        return this.stepStatus;
    }

    @Override
    public Mono<Boolean> process() {
        return this.webClient
                    .post()
                    .uri("/payment/debit")
                    .body(BodyInserters.fromValue(this.requestDTO))
                    .retrieve()
                    .bodyToMono(PaymentResponse.class)
                    .map(r -> r.getStatus().equals(PaymentStatus.PAYMENT_APPROVED))
                    .doOnNext(b -> {
                        WorkflowStepStatus status = this.stepStatus = b ? WorkflowStepStatus.COMPLETE : WorkflowStepStatus.FAILED;
                        log.info("Inventory Step Status: {}", status);
                    });
    }

    @Override
    public Mono<Boolean> revert() {
        return this.webClient
                .post()
                .uri("/payment/credit")
                .body(BodyInserters.fromValue(this.requestDTO))
                .retrieve()
                .bodyToMono(Void.class)
                .map(r -> true)
                .onErrorReturn(false);
    }

}
