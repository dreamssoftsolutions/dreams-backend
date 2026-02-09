package com.dreamssoftsolutions.backend.controller;

import com.dreamssoftsolutions.backend.model.CareerApplication;
import com.dreamssoftsolutions.backend.service.CareerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/career")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class CareerController {

    @Autowired
    private CareerService careerService;

    @PostMapping("/apply")
    public ResponseEntity<String> submitCareerApplication(
            @RequestParam("fullName") String fullName,
            @RequestParam("email") String email,
            @RequestParam("position") String position,
            @RequestParam("resume") MultipartFile resume) {
        
        try {
            // Validate inputs
            if (fullName == null || fullName.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Full name is required");
            }
            
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            
            if (position == null || position.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Position is required");
            }
            
            if (resume == null || resume.isEmpty()) {
                return ResponseEntity.badRequest().body("Resume file is required");
            }
            
            // Validate file type
            String fileName = resume.getOriginalFilename();
            if (fileName == null || (!fileName.endsWith(".pdf") && 
                !fileName.endsWith(".doc") && !fileName.endsWith(".docx"))) {
                return ResponseEntity.badRequest()
                    .body("Only PDF, DOC, and DOCX files are allowed");
            }
            
            // Validate file size (max 5MB)
            if (resume.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body("File size must be less than 5MB");
            }
            
            // Process the application
            careerService.processCareerApplication(fullName, email, position, resume);
            
            return ResponseEntity.ok("Thank you! Your application has been submitted successfully.");
            
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                .body("Error processing your application. Please try again.");
        }
    }

    @GetMapping
    public ResponseEntity<List<CareerApplication>> getAllApplications() {
        return ResponseEntity.ok(careerService.getAllApplications());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CareerApplication> getApplicationById(@PathVariable Long id) {
        return careerService.getApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}