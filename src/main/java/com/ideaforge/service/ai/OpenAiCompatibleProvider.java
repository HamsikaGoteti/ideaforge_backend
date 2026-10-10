package com.ideaforge.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaforge.dto.AiGenerateResponse;
import com.ideaforge.entity.Idea;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class OpenAiCompatibleProvider implements AiProviderAdapter {

    @Value("${app.ai.api-key:}")
    private String apiKey;

    @Value("${app.ai.api-url:https://api.openai.com/v1/chat/completions}")
    private String apiUrl;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.ai.timeout-ms:15000}")
    private int timeoutMs;

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public OpenAiCompatibleProvider(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public String getProviderName() {
        return "openai-compatible (" + model + ")";
    }

    @Override
    public AiGenerateResponse generateBlueprint(Idea idea, String customPrompt) throws Exception {
        if (apiKey == null || apiKey.trim().isEmpty() || apiKey.equalsIgnoreCase("your_api_key_here")) {
            throw new IllegalStateException("AI API Key is not configured. Please set AI_API_KEY environment variable.");
        }

        String systemPrompt = "You are an expert AI software architect and startup validator. " +
                "Generate a detailed, comprehensive, structured JSON blueprint for an idea in " +
                idea.getMode().getDisplayName() + " mode. " +
                "Return ONLY valid JSON with no markdown backticks, no markdown formatting, and no commentary. " +
                "All keys should correspond directly to the technical and strategic requirements of this mode.";

        String userPrompt = "Idea Title: " + idea.getTitle() + "\n" +
                "Tagline: " + (idea.getTagline() != null ? idea.getTagline() : "") + "\n" +
                "Mode: " + idea.getMode().name() + " (" + idea.getMode().getDisplayName() + ")\n" +
                "Target Audience: " + (idea.getTargetAudience() != null ? idea.getTargetAudience() : "") + "\n" +
                "Problem Statement: " + (idea.getProblemStatement() != null ? idea.getProblemStatement() : "") + "\n" +
                "Proposed Solution: " + (idea.getProposedSolution() != null ? idea.getProposedSolution() : "") + "\n" +
                "Tech Stack: " + (idea.getTechStack() != null ? idea.getTechStack() : "") + "\n" +
                "Key Features: " + (idea.getKeyFeatures() != null ? idea.getKeyFeatures() : "") + "\n" +
                (customPrompt != null && !customPrompt.isBlank() ? "Additional Instructions: " + customPrompt + "\n" : "");

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", List.of(
                Map.of("role", "system", "content", systemPrompt),
                Map.of("role", "user", "content", userPrompt)
        ));
        requestBody.put("temperature", 0.7);

        String jsonPayload = objectMapper.writeValueAsString(requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey.trim())
                .timeout(Duration.ofMillis(timeoutMs))
                .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new RuntimeException("AI Provider HTTP error (" + response.statusCode() + "): " + response.body());
        }

        JsonNode rootNode = objectMapper.readTree(response.body());
        JsonNode choices = rootNode.get("choices");
        if (choices == null || !choices.isArray() || choices.isEmpty()) {
            throw new RuntimeException("Malformed response from AI provider: No choices array found.");
        }

        String rawContent = choices.get(0).path("message").path("content").asText();
        if (rawContent == null || rawContent.isBlank()) {
            throw new RuntimeException("Empty completion content received from AI provider.");
        }

        // Clean any code fences if LLM included them
        String cleanJson = rawContent.trim();
        if (cleanJson.startsWith("```json")) {
            cleanJson = cleanJson.substring(7);
        } else if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.substring(3);
        }
        if (cleanJson.endsWith("```")) {
            cleanJson = cleanJson.substring(0, cleanJson.length() - 3);
        }
        cleanJson = cleanJson.trim();

        // Validate that it's valid JSON
        objectMapper.readTree(cleanJson);

        return new AiGenerateResponse(
                idea.getId(),
                cleanJson,
                "AI (" + getProviderName() + ")",
                false,
                model
        );
    }
}
