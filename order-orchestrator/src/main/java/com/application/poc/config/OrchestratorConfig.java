package com.application.poc.config;

import com.application.poc.model.OrchestratorRequest;
import com.application.poc.model.OrchestratorResponse;
import com.application.poc.service.OrchestratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.function.Function;

@Configuration
public class OrchestratorConfig {

    @Autowired
    private OrchestratorService orchestratorService;

    @Bean
    public Function<Flux<OrchestratorRequest>, Flux<OrchestratorResponse>> processor(){
        return flux -> flux
                            .flatMap(dto -> this.orchestratorService.orderProduct(dto))
                            .doOnNext(dto -> System.out.println("Status : " + dto.getStatus()));
    }
    @Bean
    public Sinks.Many<OrchestratorRequest> sink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }

}
