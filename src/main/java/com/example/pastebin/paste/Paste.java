package com.example.pastebin.paste;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "pastes")
@Getter
@Setter
@NoArgsConstructor
public class Paste {

    @Id
    @Column(name = "shortlink", length = 7)
    private String shortlink;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "expiration_length_in_minutes")
    private Integer expirationLengthInMinutes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}