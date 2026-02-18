# Detailed Kubernetes Deployment Guide

## Patient Management Microservices System

This document provides a **step‑by‑step, detailed explanation** of how
the Patient Management system was deployed on Kubernetes using Helm,
along with the reasoning behind each step.

------------------------------------------------------------------------

# 1. Project Goal

The goal was to deploy a **production‑style microservices system**
including:

-   API Gateway
-   Auth Service
-   Patient Service
-   Billing Service (gRPC)
-   Analytics Service (Kafka consumer)
-   PostgreSQL database
-   Kafka message broker
-   OpenTelemetry Collector
-   Zipkin tracing UI
-   NGINX Ingress

The system demonstrates:

-   Synchronous communication (REST + gRPC)
-   Asynchronous communication (Kafka events)
-   Distributed tracing
-   Kubernetes deployment using Helm

------------------------------------------------------------------------

# 2. Environment Setup

## Step 1 --- Install and Start Minikube

Minikube provides a local Kubernetes cluster.

Command:

``` bash
minikube start
kubectl get nodes
```

Purpose: - Start a local Kubernetes control plane - Verify the cluster
is running

------------------------------------------------------------------------

## Step 2 --- Enable NGINX Ingress

The ingress controller allows external HTTP access into the cluster.

Command:

``` bash
minikube addons enable ingress
kubectl get pods -n ingress-nginx
```

Purpose: - Install NGINX ingress controller - Allow routing from browser
→ gateway

------------------------------------------------------------------------

# 3. Build Docker Images

Each microservice was built into its own Docker image.

Services: - patient-service - billing-service - auth-service -
analytics-service - api-gateway

Example command:

``` bash
docker build -t patient-service:1.0 .
```

Purpose: - Kubernetes runs containers, not source code - Each service
must exist as a container image

------------------------------------------------------------------------

# 4. Helm Chart Creation

A Helm chart was created to manage all deployments.

Structure:

    deploy/helm/patient-management
    ├── Chart.yaml
    ├── values.yaml
    └── templates/

Validation:

``` bash
helm lint ./deploy/helm/patient-management
```

Purpose: - Helm provides reusable and parameterized Kubernetes
deployments - Easier upgrades and configuration management

------------------------------------------------------------------------

# 5. Helm Installation

Command:

``` bash
helm upgrade --install patient-management ./deploy/helm/patient-management
```

Purpose: - Create namespace - Install initial resources - Validate Helm
→ Kubernetes communication

------------------------------------------------------------------------

# 6. Infrastructure Layer Deployment

Infrastructure was deployed before services.

------------------------------------------------------------------------

## Step 6 --- Deploy PostgreSQL

Purpose: - Persistent database for patient-service

Verification:

``` bash
kubectl get pods -n patient-management
kubectl get svc -n patient-management
```

Expected:

    patient-service-db Running

------------------------------------------------------------------------

## Step 7 --- Deploy Kafka

Purpose: - Event backbone for asynchronous communication

Flow:

    patient-service → Kafka → analytics-service

Verification:

``` bash
kubectl get pods -n patient-management
```

Expected:

    kafka Running

------------------------------------------------------------------------

## Step 8 --- Deploy Observability Stack

Components: - Zipkin (trace UI) - OTel Collector (trace aggregator)

Purpose: - Distributed tracing across services

Verification:

``` bash
kubectl get pods -n patient-management
```

Expected:

    zipkin
    otel-collector

------------------------------------------------------------------------

# 7. Application Layer Deployment

Services were deployed in dependency order.

------------------------------------------------------------------------

## Step 9 --- Deploy patient-service

Purpose: - Core domain logic - Connects to DB and Kafka

Verification:

``` bash
kubectl logs deployment/patient-service -n patient-management
```

Expected:

    Started PatientServiceApplication

------------------------------------------------------------------------

## Step 10 --- Deploy billing-service

Purpose: - gRPC service for billing account creation

Verification:

``` bash
kubectl get pods -n patient-management
```

------------------------------------------------------------------------

## Step 11 --- Deploy API Gateway

Purpose: - Single entry point to system - JWT validation - Routing to
backend services

Ingress was configured to route traffic to the gateway.

------------------------------------------------------------------------

### Map local domain

Get Minikube IP:

``` bash
minikube ip
```

Add to hosts file:

    <minikube-ip> patient.local

------------------------------------------------------------------------

## Step 12 --- Deploy auth-service

Purpose: - Authentication - JWT token generation

Verification:

``` bash
kubectl get pods -n patient-management
```

------------------------------------------------------------------------

## Step 13 --- Deploy analytics-service

Purpose: - Kafka consumer - Processes patient events

Verification:

``` bash
kubectl logs deployment/analytics-service -n patient-management
```

------------------------------------------------------------------------

# 8. End-to-End Flow

After all services were deployed:

    Client
       ↓
    Ingress
       ↓
    API Gateway
       ↓
    Auth Service
       ↓
    Patient Service
       ├── gRPC → Billing Service
       └── Kafka → Analytics Service
       ↓
    Tracing → OTel → Zipkin

------------------------------------------------------------------------

# 9. Issues Encountered

## Issue 1 --- Kafka UnknownHostException

### Error

    java.net.UnknownHostException: kafka-578d59685-5skql

### Root Cause

Kafka advertised the **pod hostname** instead of the **service DNS
name**.

Client connected to:

    kafka:9092

Kafka responded with:

    kafka-578d59685-5skql:9092

Pod hostnames are not stable in Kubernetes.

### Fix

Configure Kafka to advertise the service name:

``` yaml
- name: KAFKA_ADVERTISED_LISTENERS
  value: "PLAINTEXT://kafka:9092"
```

Restart Kafka and dependent services.

### Result

-   Services connected successfully
-   Kafka communication restored

------------------------------------------------------------------------

## Issue 2 --- Application Not Reachable via Ingress

### Problem

Application not accessible at:

    http://patient.local

### Root Cause

API Gateway service type was:

    NodePort

Ingress requires backend services to be:

    ClusterIP or LoadBalancer

### Fix

Changed gateway service type:

``` yaml
type: LoadBalancer
```

Redeployed Helm chart.

### Result

-   Ingress routing worked
-   Application accessible via browser

------------------------------------------------------------------------

# 10. Final System Verification

Command:

``` bash
kubectl get pods -n patient-management
```

Expected:

    api-gateway
    auth-service
    patient-service
    billing-service
    analytics-service
    kafka
    patient-service-db
    otel-collector
    zipkin

------------------------------------------------------------------------

# 11. Key Learnings

## Kubernetes

-   Use service DNS names, not pod hostnames
-   Deploy infrastructure before services

## Kafka

-   Must advertise service address
-   Never advertise pod or localhost

## Ingress

-   Backend services must be ClusterIP or LoadBalancer

------------------------------------------------------------------------

