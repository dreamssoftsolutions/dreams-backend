package com.dreamssoftsolutions.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class CareerEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${notification.email}")
    private String hrEmail;

    @Value("${spring.mail.from}")
    private String fromEmail;

    public void sendCareerApplicationEmail(String fullName, String email, 
                                          String position, File resumeFile) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(hrEmail);
            helper.setReplyTo(email);
            helper.setSubject("📋 New Career Application - " + position);

            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

            // Create email body
            String emailBody = 
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   NEW CAREER APPLICATION\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "👤 Candidate Name: " + fullName + "\n\n" +
                "📧 Email: " + email + "\n\n" +
                "💼 Position Applied: " + position + "\n\n" +
                "📎 Resume: Attached\n\n" +
                "🕐 Applied At: " + timestamp + "\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Reply to candidate at: " + email + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Dreams Soft Solutions - HR Department";

            helper.setText(emailBody);

            // Attach resume file
            FileSystemResource file = new FileSystemResource(resumeFile);
            helper.addAttachment(resumeFile.getName(), file);

            // Send email
            mailSender.send(message);

            System.out.println("✅ Career application email sent successfully to: " + hrEmail);

            // Optional: Send confirmation email to candidate
            sendConfirmationToCandidate(fullName, email, position);

        } catch (Exception e) {
            System.err.println("❌ Failed to send career application email: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Email sending failed", e);
        }
    }

    private void sendConfirmationToCandidate(String fullName, String email, String position) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Application Received - Dreams Soft Solutions");

            String emailBody = 
                "Dear " + fullName + ",\n\n" +
                "Thank you for applying for the position of " + position + " at Dreams Soft Solutions.\n\n" +
                "We have successfully received your application and resume. " +
                "Our HR team will review your application and get back to you within 7-10 business days.\n\n" +
                "If your profile matches our requirements, we will contact you for the next steps.\n\n" +
                "Best regards,\n" +
                "HR Department\n" +
                "Dreams Soft Solutions\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "This is an automated message. Please do not reply to this email.\n" +
                "For inquiries, contact us through our website.";

            helper.setText(emailBody);

            mailSender.send(message);

            System.out.println("✅ Confirmation email sent to candidate: " + email);

        } catch (Exception e) {
            System.err.println("⚠️ Failed to send confirmation to candidate: " + e.getMessage());
            // Don't throw exception - confirmation email failure shouldn't break the application
        }
    }
}