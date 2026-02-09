package com.dreamssoftsolutions.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email}")
    private String notificationEmail;

    public void sendContactNotification(String name, String email, String message) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            
            // Email TO (your email)
            mailMessage.setTo(notificationEmail);
            
            // Email Subject
            mailMessage.setSubject("New Contact Form Submission - Dreams Soft Solutions");
            
            // Email Body
            mailMessage.setText(
                "You have a new contact form submission!\n\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Message: " + message + "\n\n" +
                "---\n" +
                "This email was sent from Dreams Soft Solutions contact form."
            );
            
            // Reply-to (customer's email)
            mailMessage.setReplyTo(email);
            
            // Send email
            mailSender.send(mailMessage);
            
            System.out.println("✅ Email notification sent successfully!");
            
        } catch (Exception e) {
            System.err.println("❌ Failed to send email: " + e.getMessage());
        }
    }
}