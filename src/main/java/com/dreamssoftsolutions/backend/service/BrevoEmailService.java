package com.dreamssoftsolutions.backend.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class BrevoEmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${notification.email}")
    private String notificationEmail;

    @Value("${spring.mail.from}")
    private String fromEmail;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    // ✅ For Contact Form - Simple text email
    public void sendContactNotification(String name, String email, String message) {
        try {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

            String emailContent =
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "  NEW CONTACT FORM SUBMISSION\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Name: " + name + "\n" +
                "Email: " + email + "\n" +
                "Message: " + message + "\n\n" +
                "Received: " + timestamp + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

            String json = "{"
                + "\"sender\":{\"name\":\"Dreams Soft Solutions\",\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + notificationEmail + "\"}],"
                + "\"replyTo\":{\"email\":\"" + email + "\"},"
                + "\"subject\":\"New Contact Form - Dreams Soft Solutions\","
                + "\"textContent\":\"" + emailContent.replace("\n", "\\n").replace("\"", "\\\"") + "\""
                + "}";

            sendHttpRequest(json);
            System.out.println("✅ Contact email sent via Brevo API!");

        } catch (Exception e) {
            System.err.println("❌ Failed to send contact email: " + e.getMessage());
            throw new RuntimeException("Email failed: " + e.getMessage(), e);
        }
    }

    // ✅ For Career Form - Email with resume attachment
    public void sendCareerApplicationEmail(String fullName, String email,
                                           String position, String fileName,
                                           byte[] resumeBytes) {
        try {
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a"));

            // Convert resume to Base64 for attachment
            String base64Resume = Base64.getEncoder().encodeToString(resumeBytes);

            // Determine content type
            String contentType = "application/pdf";
            if (fileName.endsWith(".doc")) contentType = "application/msword";
            if (fileName.endsWith(".docx")) contentType = 
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document";

            String emailContent =
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "   NEW CAREER APPLICATION\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n\n" +
                "Name     : " + fullName + "\n" +
                "Email    : " + email + "\n" +
                "Position : " + position + "\n" +
                "Resume   : " + fileName + " (attached)\n" +
                "Applied  : " + timestamp + "\n\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n" +
                "Reply to candidate: " + email + "\n" +
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━";

            String json = "{"
                + "\"sender\":{\"name\":\"Dreams Soft Solutions\",\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + notificationEmail + "\"}],"
                + "\"replyTo\":{\"email\":\"" + email + "\"},"
                + "\"subject\":\"New Career Application - " + position + "\","
                + "\"textContent\":\"" + emailContent.replace("\n", "\\n").replace("\"", "\\\"") + "\","
                + "\"attachment\":[{"
                    + "\"content\":\"" + base64Resume + "\","
                    + "\"name\":\"" + fileName + "\","
                    + "\"type\":\"" + contentType + "\""
                + "}]"
                + "}";

            sendHttpRequest(json);
            System.out.println("✅ Career email sent via Brevo API!");

            // Send confirmation to candidate
            sendCandidateConfirmation(fullName, email, position);

        } catch (Exception e) {
            System.err.println("❌ Failed to send career email: " + e.getMessage());
            throw new RuntimeException("Email failed: " + e.getMessage(), e);
        }
    }

    // ✅ Confirmation email to candidate
    private void sendCandidateConfirmation(String fullName, String email, String position) {
        try {
            String body = "Dear " + fullName + ",\\n\\n"
                + "Thank you for applying for " + position + " at Dreams Soft Solutions.\\n\\n"
                + "We have received your application. Our HR team will review it "
                + "and contact you within 7-10 business days.\\n\\n"
                + "Best regards,\\n"
                + "HR Department\\n"
                + "Dreams Soft Solutions";

            String json = "{"
                + "\"sender\":{\"name\":\"Dreams Soft Solutions\",\"email\":\"" + fromEmail + "\"},"
                + "\"to\":[{\"email\":\"" + email + "\"}],"
                + "\"subject\":\"Application Received - Dreams Soft Solutions\","
                + "\"textContent\":\"" + body + "\""
                + "}";

            sendHttpRequest(json);
            System.out.println("✅ Confirmation sent to: " + email);

        } catch (Exception e) {
            System.err.println("⚠️ Confirmation email failed: " + e.getMessage());
        }
    }

    // ✅ Core HTTP request method - Uses HTTPS, not SMTP!
    private void sendHttpRequest(String json) throws Exception {
        OkHttpClient client = new OkHttpClient();

        RequestBody body = RequestBody.create(
            json, MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
            .url(BREVO_API_URL)
            .post(body)
            .addHeader("accept", "application/json")
            .addHeader("api-key", apiKey)
            .addHeader("content-type", "application/json")
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                String responseBody = response.body() != null ? 
                    response.body().string() : "No response body";
                throw new RuntimeException("Brevo API error " + 
                    response.code() + ": " + responseBody);
            }
            System.out.println("✅ Brevo API response: " + response.code());
        }
    }
}