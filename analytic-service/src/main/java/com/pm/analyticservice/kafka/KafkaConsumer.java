package com.pm.analyticservice.kafka;

import com.google.protobuf.InvalidProtocolBufferException;
import even.patient.PatientEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient",groupId = "analytic-service")
    public void processEvent(byte[] event){

        try {
            PatientEvent patient = PatientEvent.parseFrom(event);
            //Do some analytic related operations
            log.info("Event received successfully Patient: [" +
                    "PatientId: {}," +
                    "Patient name: {}," +
                    "Patient Email: {}] ",
                    patient.getPatientId(),
                    patient.getName(),
                    patient.getEmail());
        } catch (InvalidProtocolBufferException e) {
            log.error("Issue in parsing the Patient-Event: {} ",e.getMessage());
        }

    }
}
