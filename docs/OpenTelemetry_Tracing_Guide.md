# Distributed Tracing with OpenTelemetry and Zipkin

## Overview
This project implements distributed tracing across multiple microservices using OpenTelemetry, an OpenTelemetry Collector, and Zipkin.

The system traces requests across:
- HTTP (REST)
- gRPC
- Kafka (event-driven)

---

## Architecture Flow

Client → API Gateway → Auth Service → Patient Service  
Patient Service → gRPC → Billing Service  
Patient Service → Kafka → Analytic Service

All services participate in the same distributed trace.

---

## Dependencies (per service)

Add to each service `pom.xml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>

<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-opentelemetry</artifactId>
</dependency>
```

---

## Application Properties

```properties
management.tracing.sampling.probability=1.0
management.opentelemetry.tracing.export.otlp.endpoint=http://otel-collector:4318/v1/traces
management.opentelemetry.logging.export.otlp.endpoint=http://otel-collector:4318/v1/logs
management.otlp.metrics.export.enabled=false
```

### Kafka tracing

```properties
spring.kafka.producer.observation-enabled=true
spring.kafka.listener.observation-enabled=true
```

---

## OpenTelemetry Collector Config

Create `otel-collector-config.yaml`:

```yaml
rreceivers:
  otlp:
    protocols:
      http:
        endpoint: 0.0.0.0:4318
      grpc:
        endpoint: 0.0.0.0:4317


exporters:
  zipkin:
    endpoint: "http://zipkin:9411/api/v2/spans"
  debug:
    verbosity: detailed

service:
  pipelines:
    traces:
      receivers: [otlp]
      exporters: [debug, zipkin]
```

---

## Docker Compose (collector + Zipkin)

```yaml
services:
  otel-collector:
    image: otel/opentelemetry-collector-contrib:latest
    volumes:
      - ./otel-collector-config.yaml:/etc/otelcol/config.yaml
    command: ["--config=/etc/otelcol/config.yaml"]
    ports:
      - "4317:4317"
      - "4318:4318"

  zipkin:
    image: openzipkin/zipkin
    ports:
      - "9411:9411"
```

Zipkin UI:
http://localhost:9411

---

## gRPC Tracing

### Server interceptor (Billing Service)

```java
@Configuration
public class GrpcTracingConfig {

    @Bean
    @GrpcGlobalServerInterceptor
    public ServerInterceptor openTelemetryServerInterceptor(OpenTelemetry openTelemetry) {
        return GrpcTelemetry
                .create(openTelemetry)
                .newServerInterceptor();
    }
}
```

---

### Client interceptor (Patient Service)

```java
@Configuration
public class GrpcClientTracingConfig {

    @Bean(name = "otelClientInterceptor")
    public ClientInterceptor openTelemetryClientInterceptor(OpenTelemetry openTelemetry) {
        return GrpcTelemetry
                .create(openTelemetry)
                .newClientInterceptor();
    }
}
```

Attach to channel:

```java
ManagedChannel channel = ManagedChannelBuilder
        .forAddress(hostAddress, grpcPort)
        .usePlaintext()
        .intercept(interceptor)
        .build();
```

---

## How to run

```bash
docker compose up
```

Send request:

```
POST /patients
```

Open Zipkin:
```
http://localhost:9411
```

---

## Issues Faced and Fixes

### 1. Connection reset to collector
Cause: Protocol mismatch.  
Fix: Use HTTP endpoint on port 4318.

### 2. Collector failed due to deprecated logging exporter
Fix: Replace `logging` exporter with `debug` exporter.

### 3. Kafka trace ID mismatch
Fix: Enable Kafka observation properties.

### 4. gRPC trace missing
Cause: Using `GlobalOpenTelemetry.get()` instead of Spring bean.  
Fix: Inject `OpenTelemetry` bean from Spring.

### 5. Multiple ClientInterceptor beans
Fix: Use named bean and `@Qualifier`.

---

## Final Result

Single trace includes:

HTTP:
api-gateway → auth-service → patient-service

gRPC:
patient-service → billing-service

Kafka:
patient-service → analytic-service

All spans share the same trace ID.
