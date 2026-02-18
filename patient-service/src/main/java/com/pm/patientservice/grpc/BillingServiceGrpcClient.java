package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import billing.BillingServiceGrpc.BillingServiceBlockingStub;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.grpc.ClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.opentelemetry.api.GlobalOpenTelemetry;
import io.opentelemetry.instrumentation.grpc.v1_6.GrpcTelemetry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class BillingServiceGrpcClient extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(@Qualifier("otelClientInterceptor")ClientInterceptor interceptor,
            @Value("${billing.service.address:localhost}") String hostAddress,
            @Value("${billing.service.grpc.port:9001}") int grpcPort) {
        log.info("Connecting to Billing GRPC service at {}:{}",hostAddress,grpcPort);

        ManagedChannel channel = ManagedChannelBuilder.forAddress(hostAddress, grpcPort)
                .usePlaintext()
                .intercept(interceptor)
                .build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }

    @CircuitBreaker(name = "billingGrpc", fallbackMethod = "billingFallback")
    @Retry(name = "billingGrpc")
    @Bulkhead(name = "billingGrpc", type = Bulkhead.Type.SEMAPHORE)
    public BillingResponse createBillingAccount(String patientId,String name,String email){
        BillingRequest request = BillingRequest
                .newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();

        BillingResponse response = blockingStub
                .withDeadlineAfter(800, TimeUnit.MILLISECONDS)
                .createBillingAccount(request);
        log.info("Received response from billing service via GRPC: {}",response);
        return response;
    }


    public BillingResponse billingFallback(
            String patientId,
            String name,
            String email,
            Throwable throwable) {

        log.error("Billing fallback triggered for patient {}", patientId, throwable);

        return BillingResponse.newBuilder()
                .setAccountId("PENDING")
                .setStatus("PENDING")
                .build();
    }
}
