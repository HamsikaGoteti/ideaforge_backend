package com.ideaforge.service.ai;

import com.ideaforge.dto.AiGenerateResponse;
import com.ideaforge.entity.Idea;

public interface AiProviderAdapter {
    AiGenerateResponse generateBlueprint(Idea idea, String customPrompt) throws Exception;
    String getProviderName();
}
