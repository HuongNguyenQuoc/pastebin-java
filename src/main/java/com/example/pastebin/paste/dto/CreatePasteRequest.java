package com.example.pastebin.paste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record CreatePasteRequest(
        @NotBlank
        @JsonProperty("paste_contents")
        String pasteContents,

        @Min(1)
        @JsonProperty("expiration_length_in_minutes")
        Integer expirationLengthInMinutes
) {
}
