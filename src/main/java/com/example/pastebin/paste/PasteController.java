package com.example.pastebin.paste;

import com.example.pastebin.paste.dto.CreatePasteRequest;
import com.example.pastebin.paste.dto.CreatePasteResponse;
import com.example.pastebin.paste.dto.PasteDetailResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/pastes")
public class PasteController {
    private final PasteService pasteService;

    public PasteController(PasteService pasteService) {
        this.pasteService = pasteService;
    }

    @GetMapping("/{shortlink}")
    public PasteDetailResponse read(@PathVariable String shortlink) {
        PasteDetailResponse result = pasteService.getPaste(shortlink);
        if (result == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "paste not found");
        }
        return result;
    }

    @PostMapping
    public CreatePasteResponse create(@Valid @RequestBody CreatePasteRequest request) {
        Paste paste = pasteService.createPaste(
                request.pasteContents(),
                request.expirationLengthInMinutes()
        );
        return new CreatePasteResponse(paste.getShortlink());
    }

}
