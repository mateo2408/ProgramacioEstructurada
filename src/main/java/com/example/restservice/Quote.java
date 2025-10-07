package com.example.restservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Quote(
    @JsonProperty("id") Long id,
    @JsonProperty("text") String text,
    @JsonProperty("author") String author,
    @JsonProperty("category") String category,
    @JsonProperty("date") String date
) {
    // Constructor for creating quotes manually
    public Quote(String text, String author, String category) {
        this(System.currentTimeMillis(), text, author, category, java.time.LocalDate.now().toString());
    }
}
