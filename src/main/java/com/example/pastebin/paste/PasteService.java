package com.example.pastebin.paste;

import com.example.pastebin.paste.cache.PasteCacheService;
import com.example.pastebin.paste.dto.PasteDetailResponse;
import com.example.pastebin.paste.shortener.ShortenerService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.ObjectMapper;

import java.util.Optional;

@Service
public class PasteService {

    private final PasteRepository pasteRepository;
    private final ShortenerService shortenerService;
    private final PasteCacheService cacheService;
    private final ObjectMapper objectMapper;

    public PasteService(PasteRepository pasteRepository,
                        ShortenerService shortenerService,
                        PasteCacheService cacheService,
                        ObjectMapper objectMapper) {
        this.pasteRepository = pasteRepository;
        this.shortenerService = shortenerService;
        this.cacheService = cacheService;
        this.objectMapper = objectMapper;
    }

    public Paste createPaste(String content, Integer expirationMinutes) {
        for (int attempt = 0; attempt < 5; attempt++) {
            Paste paste = new Paste();
            paste.setShortlink(shortenerService.generateShortLink());
            paste.setContent(content);
            paste.setExpirationLengthInMinutes(expirationMinutes);
            try {
                return pasteRepository.save(paste);
            } catch (DataIntegrityViolationException e) {
                // continue with for loop cause has more shortlink match each other
            }
        }
        throw new IllegalStateException("Failed to generate a unique shortlink after 5 attempts");
    }

    public PasteDetailResponse getPaste(String shortlink) {
        String cachedKey = "paste:" + shortlink;

        String cached = cacheService.get(cachedKey);
        if (cached != null) {
            try {
                return objectMapper.readValue(cached, PasteDetailResponse.class);
            } catch (DatabindException e) {
                // cache is corrupted, ignore and fetch from database
            }
        }

        Optional<Paste> pasteOpt = pasteRepository.findById(shortlink);
        if (pasteOpt.isEmpty()) {
            return null;
        }

        Paste paste = pasteOpt.get();
        PasteDetailResponse result = new PasteDetailResponse(
                paste.getContent(),
                paste.getCreatedAt().toString(),
                paste.getExpirationLengthInMinutes()
        );

        try {
            cacheService.set(cachedKey, objectMapper.writeValueAsString(result));
        } catch (DatabindException e) {
            // ignore JSON serialization errors
        }
        return result;
    }
}
