package com.ideaforge.service.ai;

import com.ideaforge.dto.AiGenerateResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.service.ScoringService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AiService {

    private static final Logger log = LoggerFactory.getLogger(AiService.class);

    @Value("${app.ai.provider:fallback}")
    private String providerType;

    @Value("${app.ai.model:gpt-4o-mini}")
    private String modelName;

    private final OpenAiCompatibleProvider openAiProvider;
    private final DeterministicFallbackGenerator fallbackGenerator;
    private final IdeaRepository ideaRepository;
    private final ScoringService scoringService;

    public AiService(OpenAiCompatibleProvider openAiProvider,
                     DeterministicFallbackGenerator fallbackGenerator,
                     IdeaRepository ideaRepository,
                     ScoringService scoringService) {
        this.openAiProvider = openAiProvider;
        this.fallbackGenerator = fallbackGenerator;
        this.ideaRepository = ideaRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public AiGenerateResponse generateAndSaveBlueprint(Idea idea, String customPrompt) {
        AiGenerateResponse response;

        boolean tryAi = !"fallback".equalsIgnoreCase(providerType);

        if (tryAi) {
            try {
                log.info("Attempting AI blueprint generation via provider: {}", providerType);
                response = openAiProvider.generateBlueprint(idea, customPrompt);
            } catch (Exception e) {
                log.warn("AI generation failed or not configured ({}). Activating deterministic fallback engine.", e.getMessage());
                String fallbackJson = fallbackGenerator.generate(idea);
                response = new AiGenerateResponse(
                        idea.getId(),
                        fallbackJson,
                        "DETERMINISTIC_FALLBACK",
                        true,
                        "deterministic-rules-engine"
                );
            }
        } else {
            log.info("Using configured deterministic fallback engine for blueprint generation.");
            String fallbackJson = fallbackGenerator.generate(idea);
            response = new AiGenerateResponse(
                    idea.getId(),
                    fallbackJson,
                    "DETERMINISTIC_FALLBACK",
                    true,
                    "deterministic-rules-engine"
            );
        }

        idea.setBlueprintJson(response.getBlueprintJson());
        idea.setBlueprintSource(response.getSource());
        ideaRepository.save(idea);

        // Reassess scoring automatically now that a blueprint is generated
        scoringService.evaluateAndSave(idea);

        return response;
    }
}
