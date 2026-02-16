package com.dreamssoftsolutions.backend.service;

import com.dreamssoftsolutions.backend.model.CareerApplication;
import com.dreamssoftsolutions.backend.repository.CareerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
public class CareerService {

    @Autowired
    private CareerRepository careerRepository;

    @Autowired
    private CareerEmailService careerEmailService;

    public void processCareerApplication(String fullName, String email,
                                         String position, MultipartFile resume) throws Exception {

        // Save to database (no file saving to disk)
        CareerApplication application = new CareerApplication(
            fullName,
            email,
            position,
            resume.getOriginalFilename(),
            "email-only",
            resume.getSize()
        );

        careerRepository.save(application);

        // ✅ Pass filename (String) and bytes - matches new CareerEmailService
        careerEmailService.sendCareerApplicationEmail(
            fullName,
            email,
            position,
            resume.getOriginalFilename(),   // ✅ String filename
            resume.getBytes()               // ✅ byte[] content
        );

        System.out.println("✅ Career application processed: " + fullName + " - " + position);
    }

    public List<CareerApplication> getAllApplications() {
        return careerRepository.findByOrderByAppliedAtDesc();
    }

    public Optional<CareerApplication> getApplicationById(Long id) {
        return careerRepository.findById(id);
    }
}