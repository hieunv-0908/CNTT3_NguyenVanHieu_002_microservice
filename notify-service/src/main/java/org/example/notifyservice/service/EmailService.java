package org.example.notifyservice.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String from;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${notification.mail.from}") String from) {
        this.mailSender = mailSender;
        this.from = from;
    }

    public void sendBookingCreatedEmail(String recipient) {
        if (StringUtils.isEmpty(recipient)) {
            throw new IllegalArgumentException();
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(recipient);
        message.setSubject("Booking Created");
        message.setText("Booking created successfully.\n\n" +
                "From: " + from + "\n" +
                "To: " + recipient);

        mailSender.send(message);
        throw new UnsupportedOperationException();
    }
}
