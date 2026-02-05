package com.pm.billingservice.config;

import io.grpc.ServerInterceptor;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GrpcTracingConfig {

    private static final Logger log = LoggerFactory.getLogger(GrpcTracingConfig.class);

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor openTelemetryServerInterceptor(OpenTelemetry openTelemetry) {
        log.info(">>> gRPC OTEL interceptor loaded");
        return GrpcTelemetry
                .create(openTelemetry)
                .newServerInterceptor();

    }
}
