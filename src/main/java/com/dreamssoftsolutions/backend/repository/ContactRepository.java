package com.dreamssoftsolutions.backend.repository;

import com.dreamssoftsolutions.backend.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContactRepository extends JpaRepository<ContactMessage, Long> {
    
    List<ContactMessage> findByEmailContaining(String email);
    
    List<ContactMessage> findByOrderByCreatedAtDesc();
}