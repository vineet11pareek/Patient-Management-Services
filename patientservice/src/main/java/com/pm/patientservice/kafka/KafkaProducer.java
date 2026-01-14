package com.pm.patientservice.kafka;

import com.pm.patientservice.model.Patient;
import even.patient.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducer {

    private static final Logger log = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String,byte[]> kafkaTemplate;

    KafkaProducer(KafkaTemplate kafkaTemplate){
        this.kafkaTemplate=kafkaTemplate;
    }

    public void sendEvent(Patient patient){
        log.info("Patient event created for ID: {}",patient.getId());
        PatientEvent event = PatientEvent.newBuilder()
                .setPatientId(patient.getId().toString())
                .setName(patient.getName())
                .setEmail(patient.getEmail())
                .setEventType("PATIENT_CREATED")
                .build();
        try {
            kafkaTemplate.send("patient",event.toByteArray());
        } catch (Exception e) {
            log.error("Event sent has failed for event: {} with PatientID: {}","PATIENT_CREATED",patient.getId());
        }

    }

}
