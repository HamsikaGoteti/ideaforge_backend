package com.ideaforge;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaforge.dto.AiGenerateResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.ai.AiService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class AiServiceTest {

    @Autowired
    private AiService aiService;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Idea idea;

    @BeforeEach
    void setUp() {
        ideaRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(new User("AI Tester", "ai@example.com", "pass", Role.ROLE_USER));
        idea = new Idea();
        idea.setTitle("MedAssist Smart Triage");
        idea.setDescription("Clinical emergency room triage prioritization engine");
        idea.setMode(IdeaMode.HACKATHON);
        idea.setTechStack("React, Java, Spring Boot, MySQL");
        idea.setProblemStatement("Emergency department patient wait queues cause preventable delays.");
        idea.setProposedSolution("Automated acuity ranking algorithm based on real-time vitals.");
        idea.setUser(user);
        idea = ideaRepository.save(idea);
    }

    @Test
    void testAiFallbackGeneratesValidStructuredJsonBlueprint() throws Exception {
        AiGenerateResponse response = aiService.generateAndSaveBlueprint(idea, null);

        assertNotNull(response);
        assertEquals(idea.getId(), response.getIdeaId());
        assertTrue(response.isFallbackUsed(), "Should use deterministic fallback when no real AI key is configured");
        assertEquals("DETERMINISTIC_FALLBACK", response.getSource());

        // Validate that blueprint is valid JSON and contains mode specific sections
        JsonNode root = objectMapper.readTree(response.getBlueprintJson());
        assertEquals("HACKATHON", root.path("mode").asText());
        assertTrue(root.has("prototypeRoadmap"), "Hackathon blueprint must contain prototypeRoadmap");
        assertTrue(root.has("teamTaskSuggestions"), "Hackathon blueprint must contain teamTaskSuggestions");
        assertTrue(root.has("demoFlowAndPitchOutline"), "Hackathon blueprint must contain demoFlowAndPitchOutline");

        // Verify idea in DB was updated with blueprint and refreshed score
        Idea reloaded = ideaRepository.findById(idea.getId()).orElse(null);
        assertNotNull(reloaded);
        assertEquals("DETERMINISTIC_FALLBACK", reloaded.getBlueprintSource());
        assertNotNull(reloaded.getBlueprintJson());
        assertTrue(reloaded.getOverallScore() > 0);
    }

    @Test
    void testAllFiveModesGenerateDistinctStructuredBlueprints() throws Exception {
        User user = userRepository.findAll().get(0);

        for (IdeaMode mode : IdeaMode.values()) {
            Idea testIdea = new Idea();
            testIdea.setTitle("Project for " + mode.name());
            testIdea.setDescription("Comprehensive description for mode blueprint verification.");
            testIdea.setMode(mode);
            testIdea.setProblemStatement("Mode specific problem statement");
            testIdea.setProposedSolution("Mode specific proposed solution");
            testIdea.setTechStack("React, Spring Boot, MySQL");
            testIdea.setUser(user);
            testIdea = ideaRepository.save(testIdea);

            AiGenerateResponse res = aiService.generateAndSaveBlueprint(testIdea, null);
            assertNotNull(res);
            assertNotNull(res.getBlueprintJson());

            JsonNode root = objectMapper.readTree(res.getBlueprintJson());
            assertEquals(mode.name(), root.path("mode").asText());

            switch (mode) {
                case HACKATHON -> assertTrue(root.has("prototypeRoadmap") && root.has("demoFlowAndPitchOutline"));
                case COLLEGE_PROJECT -> assertTrue(root.has("softwareRequirementsSpecification") && root.has("moduleBreakdown"));
                case STARTUP -> assertTrue(root.has("businessModelAndMonetization") && root.has("validationMetricsAndKPIs"));
                case VALIDATE_IDEA -> assertTrue(root.has("coreAssumptions") && root.has("experimentPlanAndProtocols"));
                case OPEN_INNOVATION -> assertTrue(root.has("tradeOffAnalysis") && root.has("ecosystemCollaborationPlan"));
            }
        }
    }
}
