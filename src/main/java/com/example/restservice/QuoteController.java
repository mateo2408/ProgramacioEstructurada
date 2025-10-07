package com.example.restservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@RestController
@CrossOrigin(origins = "*") // Allow frontend access
public class QuoteController {

    private final QuoteService quoteService;
    private final AtomicLong counter = new AtomicLong();

    @Autowired
    public QuoteController(QuoteService quoteService) {
        this.quoteService = quoteService;
    }

    @GetMapping("/quote/daily")
    public Map<String, Object> getDailyQuote() {
        Quote quote = quoteService.getDailyQuote();
        return Map.of(
            "id", counter.incrementAndGet(),
            "quote", quote,
            "cached", true,
            "type", "daily"
        );
    }

    @GetMapping("/quote/category/{category}")
    public Map<String, Object> getQuoteByCategory(@PathVariable String category) {
        long startTime = System.currentTimeMillis();
        Quote quote = quoteService.getQuoteByCategory(category);
        long endTime = System.currentTimeMillis();

        return Map.of(
            "id", counter.incrementAndGet(),
            "quote", quote,
            "category", category,
            "responseTime", endTime - startTime,
            "cached", endTime - startTime < 100 // Assume cached if response is very fast
        );
    }

    @GetMapping("/quote/author/{author}")
    public Map<String, Object> getQuotesByAuthor(@PathVariable String author) {
        List<Quote> quotes = quoteService.getQuotesByAuthor(author);
        return Map.of(
            "id", counter.incrementAndGet(),
            "quotes", quotes,
            "author", author,
            "count", quotes.size()
        );
    }

    @GetMapping("/quote/expensive/{seed}")
    public Map<String, Object> getExpensiveQuote(@PathVariable String seed) {
        long startTime = System.currentTimeMillis();
        Quote quote = quoteService.getExpensiveQuote(seed);
        long endTime = System.currentTimeMillis();

        return Map.of(
            "id", counter.incrementAndGet(),
            "quote", quote,
            "responseTime", endTime - startTime,
            "cached", endTime - startTime < 1000,
            "message", endTime - startTime < 1000 ? "Retrieved from cache!" : "Fresh data (2s delay simulated)"
        );
    }

    @GetMapping("/quote/categories")
    public Map<String, Object> getCategories() {
        List<String> categories = quoteService.getAvailableCategories();
        return Map.of(
            "categories", categories,
            "count", categories.size()
        );
    }

    @GetMapping("/quote/random")
    public Map<String, Object> getRandomQuote() {
        List<String> categories = quoteService.getAvailableCategories();
        String randomCategory = categories.get((int) (Math.random() * categories.size()));
        Quote quote = quoteService.getQuoteByCategory(randomCategory);

        return Map.of(
            "id", counter.incrementAndGet(),
            "quote", quote,
            "type", "random"
        );
    }
}
