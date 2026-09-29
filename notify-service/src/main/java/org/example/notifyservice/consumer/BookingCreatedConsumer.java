package org.example.notifyservice.consumer;

import org.example.notifyservice.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import static org.springframework.kafka.listener.ShareConsumerRecordRecoverer.LOGGER;


@Component
public class BookingCreatedConsumer {

    private final EmailService emailService;


    public BookingCreatedConsumer(EmailService emailService) {
        this.emailService = emailService;
    }

    @KafkaListener(topics = "booking-created")
    public void consume(String email) {
        if (StringUtils.isEmpty(email)) {
            LOGGER.error("Invalid message received");
            return;
        }

        LOGGER.info("Received email");

        emailService.sendBookingCreatedEmail(email);
//        throw new UnsupportedOperationException();
    }
}
