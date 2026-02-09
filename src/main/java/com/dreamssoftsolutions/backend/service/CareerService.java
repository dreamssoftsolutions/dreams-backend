package com.dreamssoftsolutions.backend.service;

import com.dreamssoftsolutions.backend.model.CareerApplication;
import com.dreamssoftsolutions.backend.repository.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CareerEmailService careerEmailService;

    @Value("${file.upload.dir:uploads/resumes}")
    private String uploadDir;

    public void processCareerApplication(String fullName, String email, 
                                        String position, MultipartFile resume) throws IOException {
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Generate unique filename
        String originalFilename = resume.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String uniqueFilename = fullName.replaceAll("\\s+", "_") + "_" + 
                               UUID.randomUUID().toString().substring(0, 8) + 
                               fileExtension;

        // Save file to disk
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.copy(resume.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Save to database
        CareerApplication application = new CareerApplication(
            fullName,
            email,
            position,
            originalFilename,
            filePath.toString(),
            resume.getSize()
        );
        
        CareerApplication saved = careerRepository.save(application);

        // Send email notification with resume attached
        careerEmailService.sendCareerApplicationEmail(
            fullName, 
            email, 
            position, 
            filePath.toFile()
        );

        System.out.println("✅ Career application processed: " + fullName + " - " + position);
    }

    public List<CareerApplication> getAllApplications() {
        return careerRepository.findByOrderByAppliedAtDesc();
    }

    public Optional<CareerApplication> getApplicationById(Long id) {
        return careerRepository.findById(id);
    }

    public List<CareerApplication> getApplicationsByPosition(String position) {
        return careerRepository.findByPosition(position);
    }

    public void deleteApplication(Long id) {
        Optional<CareerApplication> application = careerRepository.findById(id);
        if (application.isPresent()) {
            // Delete file from disk
            try {
                Path filePath = Paths.get(application.get().getResumeFilePath());
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                System.err.println("Error deleting file: " + e.getMessage());
            }
            // Delete from database
            careerRepository.deleteById(id);
        }
    }
}