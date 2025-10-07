package com.example.restservice;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@Service
public class QuoteService {

    private final WebClient webClient;
    private final Random random = new Random();

    // Fallback quotes when external API is unavailable
    private final List<Quote> fallbackQuotes = List.of(
        new Quote("The only way to do great work is to love what you do.", "Steve Jobs", "motivation"),
        new Quote("Life is what happens to you while you're busy making other plans.", "John Lennon", "life"),
        new Quote("The future belongs to those who believe in the beauty of their dreams.", "Eleanor Roosevelt", "dreams"),
        new Quote("It is during our darkest moments that we must focus to see the light.", "Aristotle", "inspiration"),
        new Quote("The way to get started is to quit talking and begin doing.", "Walt Disney", "action"),
        new Quote("Don't let yesterday take up too much of today.", "Will Rogers", "motivation"),
        new Quote("You learn more from failure than from success.", "Unknown", "wisdom"),
        new Quote("It's not whether you get knocked down, it's whether you get up.", "Vince Lombardi", "resilience")
    );

    public QuoteService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.quotegarden.io").build();
    }

    @Cacheable(value = "quotes", key = "#category")
    public Quote getQuoteByCategory(String category) {
        try {
            // Simulate external API call with fallback
            return getRandomQuoteFromCategory(category);
        } catch (Exception e) {
            // Return cached fallback quote
            return getFallbackQuote(category);
        }
    }

    @Cacheable(value = "dailyQuote", key = "'daily-' + T(java.time.LocalDate).now()")
    public Quote getDailyQuote() {
        // This will be cached for the entire day
        return fallbackQuotes.get(random.nextInt(fallbackQuotes.size()));
    }

    @Cacheable(value = "authorQuotes", key = "#author")
    public List<Quote> getQuotesByAuthor(String author) {
        return fallbackQuotes.stream()
            .filter(quote -> quote.author().toLowerCase().contains(author.toLowerCase()))
            .toList();
    }

    private Quote getRandomQuoteFromCategory(String category) {
        // Filter quotes by category or return random if category not found
        List<Quote> categoryQuotes = fallbackQuotes.stream()
            .filter(quote -> quote.category().equalsIgnoreCase(category))
            .toList();

        if (categoryQuotes.isEmpty()) {
            return fallbackQuotes.get(random.nextInt(fallbackQuotes.size()));
        }

        return categoryQuotes.get(random.nextInt(categoryQuotes.size()));
    }

    private Quote getFallbackQuote(String category) {
        return getRandomQuoteFromCategory(category);
    }

    public List<String> getAvailableCategories() {
        return fallbackQuotes.stream()
            .map(Quote::category)
            .distinct()
            .sorted()
            .toList();
    }

    // Method to simulate expensive operation (like external API call)
    @Cacheable(value = "expensiveQuotes", key = "#seed")
    public Quote getExpensiveQuote(String seed) {
        // Simulate delay
        try {
            Thread.sleep(2000); // 2 second delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        int index = Math.abs(seed.hashCode()) % fallbackQuotes.size();
        return fallbackQuotes.get(index);
    }
}
