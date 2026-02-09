package com.dreamssoftsolutions.backend.service;

import com.dreamssoftsolutions.backend.model.ContactMessage;
import com.dreamssoftsolutions.backend.repository.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ContactService {

    @Autowired
    private ContactRepository contactRepository;

    @Autowired
private EmailService emailService;

public ContactMessage saveContactMessage(ContactMessage contactMessage) {
    // Save to database
    ContactMessage saved = contactRepository.save(contactMessage);
    
    // Send email notification
    emailService.sendContactNotification(
        saved.getName(),
        saved.getEmail(),
        saved.getMessage()
    );
    
    return saved;
}

    public List<ContactMessage> getAllMessages() {
        return contactRepository.findByOrderByCreatedAtDesc();
    }

    public Optional<ContactMessage> getMessageById(Long id) {
        return contactRepository.findById(id);
    }

    public void deleteMessage(Long id) {
        contactRepository.deleteById(id);
    }
}