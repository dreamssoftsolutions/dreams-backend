package com.dreamssoftsolutions.backend.controller;

import com.dreamssoftsolutions.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/contact")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ContactController {

    @Autowired
    private EmailService emailService;

    @PostMapping
    public ResponseEntity<String> submitContactForm(@RequestBody Map<String, String> formData) {
        try {
            String name = formData.get("name");
            String email = formData.get("email");
            String message = formData.get("message");

            if (name == null || email == null || message == null) {
                return ResponseEntity.badRequest().body("All fields required");
            }

            emailService.sendContactNotification(name, email, message);
            return ResponseEntity.ok("Message received successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}