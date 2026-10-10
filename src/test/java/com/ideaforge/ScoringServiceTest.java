package com.ideaforge;

import com.ideaforge.entity.Assessment;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.service.ScoringService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScoringServiceTest {

    @Autowired
    private ScoringService scoringService;

    @Test
    void testDeterministicEvaluationIsReproducible() {
        Idea idea = new Idea();
        idea.setTitle("AI Campus Companion");
        idea.setTagline("Automated personalized student assistance");
        idea.setDescription("An intelligent campus assistant integrating college scheduling, exam notices, and AI study group matching.");
        idea.setMode(IdeaMode.COLLEGE_PROJECT);
        idea.setProblemStatement("Students miss key university deadlines and lack structured study cohorts.");
        idea.setProposedSolution("A unified mobile and web platform with automated timetable synchronization.");
        idea.setTechStack("React, Spring Boot, MySQL, REST API");
        idea.setTargetAudience("Undergraduate engineering students");
        idea.setCurrentStage("Prototype");
        idea.setKeyFeatures("Schedule tracking, peer discussion rooms, instant notice parser");

        Assessment a1 = scoringService.evaluate(idea);
        Assessment a2 = scoringService.evaluate(idea);

        assertEquals(a1.getOverallScore(), a2.getOverallScore(), "Deterministic scores must be 100% reproducible");
        assertEquals(a1.getInnovationScore(), a2.getInnovationScore());
        assertEquals(a1.getFeasibilityScore(), a2.getFeasibilityScore());
        assertEquals(a1.getImpactScore(), a2.getImpactScore());
        assertEquals(a1.getMarketDemandScore(), a2.getMarketDemandScore());
        assertEquals(a1.getReadinessScore(), a2.getReadinessScore());
        assertEquals(a1.getEvidenceScore(), a2.getEvidenceScore());

        assertTrue(a1.getOverallScore() >= 40 && a1.getOverallScore() <= 100);
    }

    @Test
    void testDifferentModesProduceDifferentWeights() {
        Map<String, Double> hackathonWeights = scoringService.getModeWeights(IdeaMode.HACKATHON);
        Map<String, Double> collegeWeights = scoringService.getModeWeights(IdeaMode.COLLEGE_PROJECT);
        Map<String, Double> startupWeights = scoringService.getModeWeights(IdeaMode.STARTUP);
        Map<String, Double> validateWeights = scoringService.getModeWeights(IdeaMode.VALIDATE_IDEA);
        Map<String, Double> openWeights = scoringService.getModeWeights(IdeaMode.OPEN_INNOVATION);

        assertNotEquals(hackathonWeights.get("readiness"), collegeWeights.get("readiness"));
        assertNotEquals(startupWeights.get("marketDemand"), hackathonWeights.get("marketDemand"));
        assertNotEquals(validateWeights.get("evidence"), hackathonWeights.get("evidence"));
        assertNotEquals(openWeights.get("impact"), hackathonWeights.get("impact"));

        // Verify each mode's weights sum to 1.0 (100%)
        for (IdeaMode mode : IdeaMode.values()) {
            Map<String, Double> w = scoringService.getModeWeights(mode);
            double sum = w.values().stream().mapToDouble(Double::doubleValue).sum();
            assertEquals(1.0, sum, 0.001, "Weights for mode " + mode + " must sum to 1.0");
        }
    }

    @Test
    void testAllFiveModesProduceConsistentAssessments() {
        for (IdeaMode mode : IdeaMode.values()) {
            Idea idea = new Idea();
            idea.setTitle("Multi-mode Test: " + mode.name());
            idea.setDescription("Detailed description for testing mode scoring consistency across criteria.");
            idea.setMode(mode);
            idea.setProblemStatement("Clear problem statement for mode assessment.");
            idea.setProposedSolution("Comprehensive proposed solution with modern tooling.");
            idea.setTechStack("React, Java, Spring Boot, MySQL");
            idea.setCurrentStage("Prototype");

            Assessment assessment = scoringService.evaluate(idea);
            assertNotNull(assessment);
            assertEquals(mode, assessment.getMode());
            assertTrue(assessment.getOverallScore() > 0 && assessment.getOverallScore() <= 100);
            assertTrue(assessment.getInnovationScore() > 0);
            assertTrue(assessment.getFeasibilityScore() > 0);
            assertTrue(assessment.getImpactScore() > 0);
            assertTrue(assessment.getMarketDemandScore() > 0);
            assertTrue(assessment.getReadinessScore() > 0);
            assertTrue(assessment.getEvidenceScore() > 0);
        }
    }
}
