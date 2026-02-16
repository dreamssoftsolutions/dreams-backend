package com.dreamssoftsolutions.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CareerEmailService {

    @Autowired
    private BrevoEmailService brevoEmailService;

    public void sendCareerApplicationEmail(String fullName, String email,
                                           String position, String fileName,
                                           byte[] resumeBytes) {
        brevoEmailService.sendCareerApplicationEmail(
            fullName, email, position, fileName, resumeBytes
        );
    }
}