package com.ai.chat.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class SarvamAiService {

    @Value("${sarvam.api.key}")
    private String apiKey;

    @Value("${sarvam.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askSarvam(List<ChatMessage> history, String userMessage) {

        try {

            String url = "https://api.sarvam.ai/v1/chat/completions";

            List<Map<String, String>> messages = new ArrayList<>();

            messages.add(Map.of(
                    "role", "system",
                    "content", "You are a helpful AI assistant."
            ));

            for (ChatMessage msg : history) {

                messages.add(Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ));
            }

            messages.add(Map.of(
                    "role", "user",
                    "content", userMessage
            ));

            Map<String, Object> body = new HashMap<>();

            body.put("model", model);
            body.put("messages", messages);
            body.put("temperature", 0.2);
            body.put("max_tokens", 1000);

            HttpHeaders headers = new HttpHeaders();

            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            url,
                            entity,
                            Map.class
                    );

            Map responseBody = response.getBody();

            System.out.println("FULL RESPONSE = " + responseBody);

            if (responseBody == null) {
                return "No response received from Sarvam AI.";
            }

            Object choicesObj = responseBody.get("choices");

            if (choicesObj == null) {
                return "Sarvam AI did not return choices. Full Response: " + responseBody;
            }

            List choices = (List) choicesObj;

            if (choices.isEmpty()) {
                return "Sarvam AI returned empty choices.";
            }

            Map firstChoice = (Map) choices.get(0);

            if (firstChoice == null) {
                return "First choice is null.";
            }

            Map message = (Map) firstChoice.get("message");

            if (message == null) {
                return "Message object is null.";
            }

            Object contentObj = message.get("content");

            return String.valueOf(contentObj);

        } catch (Exception e) {

            e.printStackTrace();

            return "Error while calling Sarvam AI: " + e.getMessage();
        }
    }
}