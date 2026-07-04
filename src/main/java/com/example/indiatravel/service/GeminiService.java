package com.example.indiatravel.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GeminiService {
    @Value("${gemini.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GEMINI_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=";

    public String generateContent(String prompt, String systemInstruction) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();
        
        if (systemInstruction != null && !systemInstruction.isEmpty()) {
            Map<String, Object> systemInstructMap = new HashMap<>();
            systemInstructMap.put("parts", List.of(Map.of("text", systemInstruction)));
            requestBody.put("system_instruction", systemInstructMap);
        }

        Map<String, Object> contentMap = new HashMap<>();
        contentMap.put("parts", List.of(Map.of("text", prompt)));
        requestBody.put("contents", List.of(contentMap));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);
        
        try {
            // Using URI.create prevents Spring RestTemplate from incorrectly URL-encoding the ':' in ':generateContent'
            URI uri = URI.create(GEMINI_URL + apiKey);
            ResponseEntity<Map> response = restTemplate.postForEntity(uri, request, Map.class);
            Map<String, Object> body = response.getBody();
            if (body != null && body.containsKey("candidates")) {
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) body.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
                    List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                    return (String) parts.get(0).get("text");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "{\"error\": \"Failed to generate content: " + e.getMessage() + "\"}";
        }
        return "{\"error\": \"No response generated.\"}";
    }
}
