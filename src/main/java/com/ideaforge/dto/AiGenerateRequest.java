package com.ideaforge.dto;

public class AiGenerateRequest {
    private String customPrompt;

    public AiGenerateRequest() {}

    public AiGenerateRequest(String customPrompt) {
        this.customPrompt = customPrompt;
    }

    public String getCustomPrompt() {
        return customPrompt;
    }

    public void setCustomPrompt(String customPrompt) {
        this.customPrompt = customPrompt;
    }
}
