package com.dreamssoftsolutions.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private BrevoEmailService brevoEmailService;

    public void sendContactNotification(String name, String email, String message) {
        brevoEmailService.sendContactNotification(name, email, message);
    }
}