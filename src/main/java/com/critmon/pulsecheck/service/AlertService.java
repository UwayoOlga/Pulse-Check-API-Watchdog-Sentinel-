package com.critmon.pulsecheck.service;

import com.critmon.pulsecheck.domain.Monitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.Instant;
import java.util.Properties;

@Service
public class AlertService {

    private static final Logger logger = LoggerFactory.getLogger(AlertService.class);

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String host;

    @Value("${spring.mail.port:587}")
    private int port;

    @Value("${spring.mail.username:}")
    private String username;

    @Value("${spring.mail.password:}")
    private String password;

    public void fireAlert(Monitor monitor) {
        Long id = monitor.getId();
        String targetEmail = monitor.getAlertEmail();

        // Print exact JSON alert to console as required by specs
        String jsonAlert = String.format("{\"ALERT\": \"Device %d is down!\", \"time\": \"%s\"}", 
                                         id, Instant.now().toString());
        System.out.println(jsonAlert);

        try {
            sendEmail(targetEmail, id);
        } catch (Exception e) {
            logger.error("Failed to send alert email to {}: {}", targetEmail, e.getMessage());
        }
    }

    private void sendEmail(String to, Long id) throws MessagingException {
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(username));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject("CRITICAL: Device " + id + " is Offline");
        
        String text = "The watchdog sentinel has detected that device [" + id + 
                      "] has missed its heartbeat deadline and is now marked as DOWN.\n\n" +
                      "Timestamp: " + Instant.now().toString() + "\n" +
                      "This is an automated alert from Pulse-Check-API.";
        
        message.setText(text);

        Transport.send(message);
    }
}
