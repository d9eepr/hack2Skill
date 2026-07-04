package com.example.indiatravel.controller;

import com.example.indiatravel.model.UserSession;
import com.example.indiatravel.repository.UserSessionRepository;
import com.example.indiatravel.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*") // For Lovable frontend compatibility
public class ApiController {

    @Autowired
    private GeminiService geminiService;

    @Autowired
    private UserSessionRepository sessionRepository;

    @PostMapping("/chat")
    public String chat(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String message = request.get("message");
        
        // Simple chat history management
        String history = "";
        if (sessionId != null) {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
            if (sessionOpt.isPresent()) {
                history = sessionOpt.get().getChatHistory();
            } else {
                UserSession newSession = new UserSession();
                newSession.setSessionId(sessionId);
                newSession.setChatHistory("");
                sessionRepository.save(newSession);
            }
        }

        String systemInstruction = "You are an expert Indian travel guide. You recommend authentic experiences, hidden gems, and cultural hotspots. Provide a conversational, helpful, and culturally rich response.";
        String prompt = "Chat History:\n" + history + "\n\nUser: " + message + "\nGuide: ";
        
        String response = geminiService.generateContent(prompt, systemInstruction);

        if (sessionId != null) {
            Optional<UserSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
            if (sessionOpt.isPresent()) {
                UserSession s = sessionOpt.get();
                s.setChatHistory(history + "\nUser: " + message + "\nGuide: " + response);
                sessionRepository.save(s);
            }
        }
        
        return response;
    }

    @PostMapping("/story")
    public String generateStory(@RequestBody Map<String, String> request) {
        String location = request.get("location");
        String systemInstruction = "You are an expert storyteller specialized in Indian heritage. Create an immersive narrative uncovering the history and hidden gems of the provided location. Output strictly in valid JSON format with keys: title, narrative, hiddenGem, historicalSignificance.";
        String prompt = "Tell me a heritage story and uncover a hidden gem about: " + location;
        
        return geminiService.generateContent(prompt, systemInstruction);
    }

    @PostMapping("/events")
    public String getEvents(@RequestBody Map<String, String> request) {
        String location = request.get("location");
        String month = request.get("month");
        String systemInstruction = "You are a local event coordinator in India. Suggest authentic local cultural events, festivals, or local activities. Output strictly in valid JSON format: an array of objects with keys: name, description, dates, culturalSignificance.";
        String prompt = "What are some local events or festivals happening in or around " + location + " during the month of " + month + "?";
        
        return geminiService.generateContent(prompt, systemInstruction);
    }

    @PostMapping("/itinerary")
    public String generateItinerary(@RequestBody Map<String, String> request) {
        String location = request.get("location");
        String days = request.get("days");
        String preferences = request.get("preferences");
        
        String systemInstruction = "You are an expert Indian travel planner. Generate a detailed, day-by-day itinerary focusing on deep cultural immersion and authentic experiences, avoiding tourist traps where possible. Output strictly in valid JSON format: { \"destination\": \"...\", \"duration\": \"...\", \"days\": [ { \"day\": 1, \"activities\": [ { \"time\": \"...\", \"title\": \"...\", \"description\": \"...\" } ] } ] }.";
        String prompt = "Create a " + days + "-day itinerary for " + location + ". Preferences: " + preferences;
        
        return geminiService.generateContent(prompt, systemInstruction);
    }
}
