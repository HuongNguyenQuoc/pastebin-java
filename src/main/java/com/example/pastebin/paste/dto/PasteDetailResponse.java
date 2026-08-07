package com.example.pastebin.paste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record PasteDetailResponse(
        @JsonProperty("paste_contents") String pasteContents,
        @JsonProperty("created_at") String createdAt,
        @JsonProperty("expiration_length_in_minutes") Integer expirationLengthInMinutes
) {
}
