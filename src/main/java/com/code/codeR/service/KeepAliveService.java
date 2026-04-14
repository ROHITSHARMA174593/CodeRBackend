package com.code.codeR.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepAliveService {

    @PersistenceContext
    private EntityManager entityManager;

    // 48 hours = 172800000 milliseconds 
    @Scheduled(fixedRate = 172800000)
    public void keepDatabaseAlive() {
        try {
            entityManager.createNativeQuery("SELECT 1").getSingleResult();
            System.out.println("✅ Supabase DB is alive (Ping success)");
        } catch (Exception e) {
            System.err.println("❌ KeepAlive failed: " + e.getMessage());
        }
    }
}