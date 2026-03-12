package com.dreamssoftsolutions.backend.controller;

import com.dreamssoftsolutions.backend.service.CareerEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/career")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CareerController {

    @Autowired
    private CareerEmailService careerEmailService;

    @PostMapping("/apply")
    public ResponseEntity<String> submitCareerApplication(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("position") String position,
            @RequestParam("resume") MultipartFile resume) {

        try {
            if (fullName == null || email == null || position == null || resume.isEmpty()) {
                return ResponseEntity.badRequest().body("All fields required");
            }

            String fileName = resume.getOriginalFilename();
            if (!fileName.endsWith(".pdf") && !fileName.endsWith(".doc") && !fileName.endsWith(".docx")) {
                return ResponseEntity.badRequest().body("Only PDF, DOC, DOCX allowed");
            }

            if (resume.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body("File must be under 5MB");
            }

            careerEmailService.sendCareerApplicationEmail(
                fullName, email, position, fileName, resume.getBytes()
            );

            return ResponseEntity.ok("Application submitted successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}