package com.pm.analyticservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class AnalyticserviceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AnalyticserviceApplication.class, args);
    }

}
