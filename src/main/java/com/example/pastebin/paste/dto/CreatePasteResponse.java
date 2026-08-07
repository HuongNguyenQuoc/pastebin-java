package com.example.pastebin.paste.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record CreatePasteResponse(
        @JsonProperty("shortlink") String shortLink
) {
}
