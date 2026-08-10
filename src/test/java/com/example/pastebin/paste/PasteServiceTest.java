package com.example.pastebin.paste;

import com.example.pastebin.paste.cache.PasteCacheService;
import com.example.pastebin.paste.dto.PasteDetailResponse;
import com.example.pastebin.paste.shortener.ShortenerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import tools.jackson.databind.json.JsonMapper;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PasteServiceTest {

    @Mock
    private PasteRepository pasteRepository;

    @Mock
    private PasteCacheService cacheService;

    @Mock
    private ShortenerService shortenerService;

    private PasteService pasteService;

    @BeforeEach
    void setUp() {
        JsonMapper jsonMapper = JsonMapper.builder().build();
        pasteService = new PasteService(pasteRepository, shortenerService, cacheService, jsonMapper);
    }

    @Test
    void createPaste_SuccessFirstAttempt() {
        // Implement test logic for successful paste creation on the first attempt
        when(shortenerService.generateShortLink()).thenReturn("aB3dK9x");
        when(pasteRepository.save(any(Paste.class))).thenAnswer(invocation -> invocation.getArguments()[0]);

        Paste result = pasteService.createPaste("Hello, World!", 10);
        assertThat(result.getShortlink()).isEqualTo("aB3dK9x");
        verify(shortenerService, times(1)).generateShortLink();
    }

    @Test
    void createPaste_TryWhenShortlinkCollision() {
        // Implement test logic for try again when the short link duplicate

        when(shortenerService.generateShortLink()).thenReturn("trung01")
                .thenReturn("moi0002");
        when(pasteRepository.save(any(Paste.class))).thenThrow(new DataIntegrityViolationException("Duplicate shortlink"))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        var result = pasteService.createPaste("Hello, World!", 10);
        assertThat(result.getShortlink()).isEqualTo("moi0002");
        verify(shortenerService, times(2)).generateShortLink();
    }

    @Test
    void createPaste_FailAfter5Attempts() {
        // Implement test logic for fail after 5 attempts
        when(shortenerService.generateShortLink()).thenReturn("trung01");
        when(pasteRepository.save(any(Paste.class))).thenThrow(new DataIntegrityViolationException("Duplicate shortlink"));

        assertThatThrownBy(() -> pasteService.createPaste("Hello, World!", 10))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Failed to generate a unique shortlink after 5 attempts");

        verify(shortenerService, times(5)).generateShortLink();
    }

    @Test
    void getPaste_FromCacheIfAvailable() {
        // Implement test logic for getting paste from cache if available
        String cachedJson = """
                    {
                    "paste_contents": "Hello, World!",
                    "created_at": "2026-01-01T00:00:00",
                    "expiration_length_in_minutes": 10
                    }
                """;
        when(cacheService.get("paste:aB3dK9x")).thenReturn(cachedJson);

        PasteDetailResponse result = pasteService.getPaste("aB3dK9x");

        assertThat(result.pasteContents()).isEqualTo("Hello, World!");
        verify(pasteRepository, never()).findById(anyString());
    }

    @Test
    void getPaste_FromDatabaseIfCacheMiss() {
        // Implement test logic for getting paste from databse if cache not available
        when(cacheService.get("paste:aB3dK9x")).thenReturn(null);
        Paste paste = new Paste();
        paste.setShortlink("aB3dK9x");
        paste.setContent("Hello, World!");
        paste.setExpirationLengthInMinutes(10);
        paste.setCreatedAt(LocalDateTime.of(2026, 1, 1, 0, 0));
        when(pasteRepository.findById("aB3dK9x")).thenReturn(Optional.of(paste));

        PasteDetailResponse result = pasteService.getPaste("aB3dK9x");

        assertThat(result.pasteContents()).isEqualTo("Hello, World!");
        verify(cacheService).set(eq("paste:aB3dK9x"), any(String.class));
    }

    @Test
    void getPaste_FromDatabaseNotFound() {
        // Implement test logic for getting paste from databse if a cache not available and not found in database
        when(cacheService.get("paste:aB3dK9x")).thenReturn(null);
        when(pasteRepository.findById("aB3dK9x")).thenReturn(Optional.empty());

        PasteDetailResponse result = pasteService.getPaste("aB3dK9x");

        assertThat(result).isNull();
    }
}