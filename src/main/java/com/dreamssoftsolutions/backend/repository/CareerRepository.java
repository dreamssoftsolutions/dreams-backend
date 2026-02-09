package com.dreamssoftsolutions.backend.repository;

import com.dreamssoftsolutions.backend.model.CareerApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CareerRepository extends JpaRepository<CareerApplication, Long> {
    
    List<CareerApplication> findByPosition(String position);
    
    List<CareerApplication> findByOrderByAppliedAtDesc();
    
    List<CareerApplication> findByEmailContaining(String email);
}