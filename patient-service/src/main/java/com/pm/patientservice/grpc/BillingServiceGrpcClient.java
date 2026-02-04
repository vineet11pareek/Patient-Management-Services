package com.pm.patientservice.grpc;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import billing.BillingServiceGrpc.BillingServiceBlockingStub;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class BillingServiceGrpcClient extends BillingServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(BillingServiceGrpcClient.class);
    private final BillingServiceBlockingStub blockingStub;

    public BillingServiceGrpcClient(
            @Value("${billing.service.address:localhost}") String hostAddress,
            @Value("${billing.service.grpc.port:9001}") int grpcPort) {
        log.info("Connecting to Billing GRPC service at {}:{}",hostAddress,grpcPort);
        ManagedChannel channel = ManagedChannelBuilder.forAddress(hostAddress, grpcPort)
                .usePlaintext()
                .build();
        blockingStub = BillingServiceGrpc.newBlockingStub(channel);

    }

    public BillingResponse createBillingAccount(String patientId,String name,String email){
        BillingRequest request = BillingRequest
                .newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();

        BillingResponse response = blockingStub.createBillingAccount(request);
        log.info("Received response from billing service via GRPC: {}",response);
        return response;
    }
}
