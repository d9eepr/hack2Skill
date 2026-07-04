package com.example.indiatravel.service;

import org.springframework.stereotype.Service;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class ImageService {

    public String generateImageUrl(String location) {
        String prompt = "A beautiful, cinematic, highly detailed travel photography shot of " + location + ", India, vibrant colors, 4k resolution";
        try {
            String encodedPrompt = URLEncoder.encode(prompt, StandardCharsets.UTF_8.toString());
            // Appending a random query param to ensure fresh generation or bypass aggressive caching if needed, 
            // though pollinations usually caches identical prompts nicely.
            return "https://image.pollinations.ai/prompt/" + encodedPrompt + "?width=1200&height=600&nologo=true";
        } catch (Exception e) {
            return "";
        }
    }
}
