package com.ideaforge.dto;

import java.time.LocalDateTime;

public class AiGenerateResponse {
    private Long ideaId;
    private String blueprintJson;
    private String source; // e.g. "AI (openai:gpt-4o-mini)" or "DETERMINISTIC_FALLBACK"
    private boolean fallbackUsed;
    private String model;
    private LocalDateTime generatedAt;

    public AiGenerateResponse() {}

    public AiGenerateResponse(Long ideaId, String blueprintJson, String source, boolean fallbackUsed, String model) {
        this.ideaId = ideaId;
        this.blueprintJson = blueprintJson;
        this.source = source;
        this.fallbackUsed = fallbackUsed;
        this.model = model;
        this.generatedAt = LocalDateTime.now();
    }

    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }
    public String getBlueprintJson() { return blueprintJson; }
    public void setBlueprintJson(String blueprintJson) { this.blueprintJson = blueprintJson; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public boolean isFallbackUsed() { return fallbackUsed; }
    public void setFallbackUsed(boolean fallbackUsed) { this.fallbackUsed = fallbackUsed; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public LocalDateTime getGeneratedAt() { return generatedAt; }
    public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
}
