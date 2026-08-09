package com.example.pastebin.cleanup;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class CleanupService {

    private final JdbcTemplate jdbcTemplate;

    public CleanupService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Scheduled(fixedRate = 10 * 60 * 1000) // Run every 10 minutes
    public void deleteExpiredPastes() {
        jdbcTemplate.update("""
                DELETE FROM pastes
                WHERE expiration_length_in_minutes IS NOT NULL
                AND created_at + (expiration_length_in_minutes || ' minutes')::interval < NOW()
                """);
    }
}