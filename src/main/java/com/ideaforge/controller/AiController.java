package com.ideaforge.controller;

import com.ideaforge.dto.AiGenerateRequest;
import com.ideaforge.dto.AiGenerateResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.service.IdeaService;
import com.ideaforge.service.ai.AiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AiController {

    private final AiService aiService;
    private final IdeaService ideaService;

    @Value("${app.ai.provider:fallback}")
    private String provider;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String model;

    @Value("${app.ai.api-key:}")
    private String apiKey;

    public AiController(AiService aiService, IdeaService ideaService) {
        this.aiService = aiService;
        this.ideaService = ideaService;
    }

    @PostMapping("/ideas/{id}/blueprint/generate")
    public ResponseEntity<AiGenerateResponse> generateBlueprint(
            @PathVariable Long id,
            @RequestBody(required = false) AiGenerateRequest request) {
        Idea idea = ideaService.getIdeaEntity(id);
        String customPrompt = request != null ? request.getCustomPrompt() : null;

        AiGenerateResponse response = aiService.generateAndSaveBlueprint(idea, customPrompt);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/ai/status")
    public ResponseEntity<Map<String, Object>> getAiStatus() {
        boolean hasKey = StringUtils.hasText(apiKey) && !apiKey.equalsIgnoreCase("your_api_key_here");
        return ResponseEntity.ok(Map.of(
                "provider", provider,
                "model", model,
                "configured", hasKey,
                "statusMessage", hasKey ? "Active AI provider ready" : "Deterministic fallback engine active (No AI key configured)"
        ));
    }
}
