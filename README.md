# 🧩 Patient Management System

## 📌 Overview
This project demonstrates a **microservices-based architecture** built using modern backend technologies.
Each service is independently deployable and communicates via REST APIs and/or asynchronous messaging.

---

## 🏗 Architecture Overview
Client → API Gateway → Multiple Microservices → Databases

---

## 🛠 Tech Stack
- Java 21+
- Spring Boot
- Maven
- REST APIs
- H2 / PostgreSQL
- Docker
- Openapi v2.7.0
- gRPC

---

## 📦 Services
- API Gateway


---

## 🚀 Getting Started

### Prerequisites
- Java 21+
- Maven
- Git

### Run Locally
```bash
git clone https://github.com/vineet11pareek/Patient-Management-Services.git
cd microservices-sample
mvn clean install
```

Run a service:
```bash
mvn spring-boot:run
```

---

## 🧪 API Testing
Use Postman, curl, or REST clients.

Example:
```bash
curl http://localhost:4000/api/patients
```

---

## 🐳 Docker
```bash
docker build -t patient-service .
docker run -p 4000:4000 patient-service
```

---

## 📄 License
MIT License

## 📄 Issues
${os.detected.classifier} cannot be resolved
* Add the below dependency 
```bash
        <dependency>
            <groupId>io.netty</groupId>
            <artifactId>netty-tcnative-boringssl-static</artifactId>
            <version>2.0.69.Final</version>
            <classifier>${os.detected.classifier}</classifier>
        </dependency>
```
* Under the billing-service, sometimes Grpc services extends the compile classes from target folder of maven but it didnt recognise,so add mannually using the intellij option, select grpc-java, java folder and right click and then mark directory as "Generated source root"

---

## ✨ Author
Vineet Pareek
