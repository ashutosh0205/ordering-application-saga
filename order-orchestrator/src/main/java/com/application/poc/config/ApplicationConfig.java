package com.application.poc.config;

import com.application.poc.model.OrchestratorRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Sinks;

@Configuration
public class ApplicationConfig
{
    @Bean
    public Sinks.Many<OrchestratorRequest> sink(){
        return Sinks.many().unicast().onBackpressureBuffer();
    }

}
