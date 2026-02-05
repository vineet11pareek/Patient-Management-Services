package com.pm.patientservice.configuration;

import io.grpc.ClientInterceptor;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcClientTracingConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcClientTracingConfig.class);

    @Bean
    @Qualifier("otelClientInterceptor")
    public ClientInterceptor openTelemetryClientInterceptor(OpenTelemetry openTelemetry) {
        log.info(">>> gRPC CLIENT OTEL interceptor loaded");
        return GrpcTelemetry
                .create(openTelemetry)
                .newClientInterceptor();
    }
}
