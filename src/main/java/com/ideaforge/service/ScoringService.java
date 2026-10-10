package com.ideaforge.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideaforge.dto.ScoreBreakdownResponse;
import com.ideaforge.dto.ScoreBreakdownResponse.CriterionDetail;
import com.ideaforge.entity.Assessment;
import com.ideaforge.entity.Experiment;
import com.ideaforge.entity.ExperimentStatus;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.repository.AssessmentRepository;
import com.ideaforge.repository.IdeaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class ScoringService {

    public static final String SCORING_VERSION = "v1.2-deterministic";

    private final AssessmentRepository assessmentRepository;
    private final IdeaRepository ideaRepository;
    private final com.ideaforge.repository.ExperimentRepository experimentRepository;
    private final com.ideaforge.repository.FeedbackRepository feedbackRepository;
    private final ObjectMapper objectMapper;

    public ScoringService(AssessmentRepository assessmentRepository,
                          IdeaRepository ideaRepository,
                          com.ideaforge.repository.ExperimentRepository experimentRepository,
                          com.ideaforge.repository.FeedbackRepository feedbackRepository,
                          ObjectMapper objectMapper) {
        this.assessmentRepository = assessmentRepository;
        this.ideaRepository = ideaRepository;
        this.experimentRepository = experimentRepository;
        this.feedbackRepository = feedbackRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public Assessment evaluateAndSave(Idea idea) {
        Assessment assessment = evaluate(idea);
        Assessment saved = assessmentRepository.save(assessment);
        
        idea.setOverallScore(saved.getOverallScore());
        ideaRepository.save(idea);
        
        return saved;
    }

    public Assessment evaluate(Idea idea) {
        IdeaMode mode = idea.getMode() != null ? idea.getMode() : IdeaMode.HACKATHON;

        int innovationScore = calculateInnovationScore(idea);
        int feasibilityScore = calculateFeasibilityScore(idea);
        int impactScore = calculateImpactScore(idea);
        int marketDemandScore = calculateMarketDemandScore(idea);
        int readinessScore = calculateReadinessScore(idea);
        int evidenceScore = calculateEvidenceScore(idea);

        Map<String, Double> weights = getModeWeights(mode);

        double totalWeighted = (innovationScore * weights.get("innovation"))
                + (feasibilityScore * weights.get("feasibility"))
                + (impactScore * weights.get("impact"))
                + (marketDemandScore * weights.get("marketDemand"))
                + (readinessScore * weights.get("readiness"))
                + (evidenceScore * weights.get("evidence"));

        int overallScore = (int) Math.round(totalWeighted);
        overallScore = Math.max(0, Math.min(100, overallScore));

        List<String> strengths = new ArrayList<>();
        List<String> weaknesses = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        Map<String, CriterionDetail> criteriaDetails = new LinkedHashMap<>();

        // 1. Innovation
        String innExp = getInnovationExplanation(innovationScore, mode);
        criteriaDetails.put("innovation", new CriterionDetail("Innovation & Differentiation", innovationScore, weights.get("innovation"), innExp));
        if (innovationScore >= 70) strengths.add("Strong unique value proposition and clear differentiation.");
        else weaknesses.add("Differentiation from existing solutions could be highlighted more clearly.");

        // 2. Feasibility
        String feasExp = getFeasibilityExplanation(feasibilityScore, mode);
        criteriaDetails.put("feasibility", new CriterionDetail("Feasibility & Architecture", feasibilityScore, weights.get("feasibility"), feasExp));
        if (feasibilityScore >= 70) strengths.add("Concrete technical stack and realistic execution scope.");
        else {
            weaknesses.add("Technical architecture or tech stack requires deeper specification.");
            recommendations.add("Define the exact backend/frontend libraries, database schema, and deployment boundaries.");
        }

        // 3. Impact
        String impExp = getImpactExplanation(impactScore, mode);
        criteriaDetails.put("impact", new CriterionDetail("Potential Impact", impactScore, weights.get("impact"), impExp));
        if (impactScore >= 70) strengths.add("Broad impact with well-defined beneficiaries.");
        else recommendations.add("Clarify quantitative impact metrics and target beneficiary numbers.");

        // 4. Market / User Need
        String mktExp = getMarketExplanation(marketDemandScore, mode);
        criteriaDetails.put("marketDemand", new CriterionDetail("User Need & Assumptions", marketDemandScore, weights.get("marketDemand"), mktExp));
        if (marketDemandScore >= 70) strengths.add("Clear user pain point and problem definition.");
        else weaknesses.add("Core problem statement lacks granular user pain point details.");

        // 5. Readiness
        String readyExp = getReadinessExplanation(readinessScore, idea.getCurrentStage());
        criteriaDetails.put("readiness", new CriterionDetail("Implementation Readiness", readinessScore, weights.get("readiness"), readyExp));
        if (readinessScore < 60) {
            recommendations.add("Advance stage from Concept to Prototype by fleshing out MVP feature specifications.");
        }

        // 6. Validation Evidence
        String evExp = getEvidenceExplanation(evidenceScore, idea);
        criteriaDetails.put("evidence", new CriterionDetail("Validation Evidence", evidenceScore, weights.get("evidence"), evExp));
        if (evidenceScore < 50) {
            weaknesses.add("Limited empirical evidence or test validation recorded yet.");
            recommendations.add("Create at least 1 experiment with testable hypothesis and gather user feedback.");
        } else {
            strengths.add("Active validation through recorded feedback and structured experiments.");
        }

        Assessment assessment = new Assessment();
        assessment.setIdea(idea);
        assessment.setMode(mode);
        assessment.setOverallScore(overallScore);
        assessment.setInnovationScore(innovationScore);
        assessment.setFeasibilityScore(feasibilityScore);
        assessment.setImpactScore(impactScore);
        assessment.setMarketDemandScore(marketDemandScore);
        assessment.setReadinessScore(readinessScore);
        assessment.setEvidenceScore(evidenceScore);
        assessment.setScoringVersion(SCORING_VERSION);

        try {
            assessment.setCriterionBreakdownJson(objectMapper.writeValueAsString(criteriaDetails));
            assessment.setStrengthsJson(objectMapper.writeValueAsString(strengths));
            assessment.setWeaknessesJson(objectMapper.writeValueAsString(weaknesses));
            assessment.setRecommendationsJson(objectMapper.writeValueAsString(recommendations));
        } catch (JsonProcessingException e) {
            assessment.setCriterionBreakdownJson("{}");
            assessment.setStrengthsJson("[]");
            assessment.setWeaknessesJson("[]");
            assessment.setRecommendationsJson("[]");
        }

        return assessment;
    }

    public Map<String, Double> getModeWeights(IdeaMode mode) {
        Map<String, Double> weights = new HashMap<>();
        switch (mode) {
            case HACKATHON -> {
                weights.put("innovation", 0.25);
                weights.put("feasibility", 0.20);
                weights.put("impact", 0.15);
                weights.put("marketDemand", 0.10);
                weights.put("readiness", 0.25);
                weights.put("evidence", 0.05);
            }
            case COLLEGE_PROJECT -> {
                weights.put("innovation", 0.15);
                weights.put("feasibility", 0.25);
                weights.put("impact", 0.15);
                weights.put("marketDemand", 0.15);
                weights.put("readiness", 0.20);
                weights.put("evidence", 0.10);
            }
            case STARTUP -> {
                weights.put("innovation", 0.15);
                weights.put("feasibility", 0.15);
                weights.put("impact", 0.20);
                weights.put("marketDemand", 0.25);
                weights.put("readiness", 0.10);
                weights.put("evidence", 0.15);
            }
            case VALIDATE_IDEA -> {
                weights.put("innovation", 0.10);
                weights.put("feasibility", 0.20);
                weights.put("impact", 0.15);
                weights.put("marketDemand", 0.20);
                weights.put("readiness", 0.10);
                weights.put("evidence", 0.25);
            }
            case OPEN_INNOVATION -> {
                weights.put("innovation", 0.20);
                weights.put("feasibility", 0.15);
                weights.put("impact", 0.25);
                weights.put("marketDemand", 0.15);
                weights.put("readiness", 0.10);
                weights.put("evidence", 0.15);
            }
        }
        return weights;
    }

    private int calculateInnovationScore(Idea idea) {
        int score = 40;
        String text = (idea.getTitle() + " " + idea.getTagline() + " " + idea.getProposedSolution()).toLowerCase();
        if (text.length() > 50) score += 15;
        if (text.length() > 150) score += 15;
        if (StringUtils.hasText(idea.getTagline())) score += 10;
        if (StringUtils.hasText(idea.getKeyFeatures())) score += 10;
        if (idea.getBlueprintJson() != null && idea.getBlueprintJson().length() > 100) score += 10;
        return Math.min(100, score);
    }

    private int calculateFeasibilityScore(Idea idea) {
        int score = 35;
        if (StringUtils.hasText(idea.getTechStack())) {
            score += 25;
            if (idea.getTechStack().contains(",") || idea.getTechStack().split(" ").length >= 2) {
                score += 15;
            }
        }
        if (StringUtils.hasText(idea.getProposedSolution()) && idea.getProposedSolution().length() > 50) {
            score += 15;
        }
        if (StringUtils.hasText(idea.getKeyFeatures())) {
            score += 10;
        }
        return Math.min(100, score);
    }

    private int calculateImpactScore(Idea idea) {
        int score = 40;
        if (StringUtils.hasText(idea.getTargetAudience())) {
            score += 25;
        }
        if (StringUtils.hasText(idea.getDescription()) && idea.getDescription().length() > 100) {
            score += 20;
        }
        if (StringUtils.hasText(idea.getTags())) {
            score += 15;
        }
        return Math.min(100, score);
    }

    private int calculateMarketDemandScore(Idea idea) {
        int score = 35;
        if (StringUtils.hasText(idea.getProblemStatement())) {
            score += 30;
            if (idea.getProblemStatement().length() > 80) {
                score += 15;
            }
        }
        if (StringUtils.hasText(idea.getTargetAudience())) {
            score += 20;
        }
        return Math.min(100, score);
    }

    private int calculateReadinessScore(Idea idea) {
        int score = 30;
        String stage = idea.getCurrentStage() != null ? idea.getCurrentStage() : "Concept";
        switch (stage.toLowerCase()) {
            case "mvp ready", "production" -> score += 40;
            case "prototype" -> score += 30;
            case "in validation" -> score += 20;
            default -> score += 10;
        }
        if (StringUtils.hasText(idea.getKeyFeatures())) score += 15;
        if (idea.getBlueprintJson() != null && !idea.getBlueprintJson().isEmpty()) score += 15;
        return Math.min(100, score);
    }

    private int calculateEvidenceScore(Idea idea) {
        int score = 25;
        int expCount = 0;
        int completedExp = 0;
        int feedbackCount = 0;

        if (idea.getId() != null && experimentRepository != null && feedbackRepository != null) {
            List<Experiment> exps = experimentRepository.findByIdeaIdOrderByCreatedAtDesc(idea.getId());
            expCount = exps.size();
            for (Experiment exp : exps) {
                if (exp.getStatus() == ExperimentStatus.COMPLETED) {
                    completedExp++;
                }
            }
            feedbackCount = feedbackRepository.findByIdeaIdOrderByCreatedAtDesc(idea.getId()).size();
        } else {
            if (idea.getExperiments() != null) {
                expCount = idea.getExperiments().size();
                for (Experiment exp : idea.getExperiments()) {
                    if (exp.getStatus() == ExperimentStatus.COMPLETED) completedExp++;
                }
            }
            feedbackCount = idea.getFeedbacks() != null ? idea.getFeedbacks().size() : 0;
        }

        score += Math.min(25, expCount * 10);
        score += Math.min(25, completedExp * 15);
        score += Math.min(25, feedbackCount * 8);

        return Math.min(100, score);
    }

    private String getInnovationExplanation(int score, IdeaMode mode) {
        if (score >= 80) return "High differentiation with novel solution framing tailored for " + mode.getDisplayName() + ".";
        if (score >= 60) return "Moderate novelty; clear proposition with room to distinguish from existing tools.";
        return "Baseline concept; needs more unique value components to stand out.";
    }

    private String getFeasibilityExplanation(int score, IdeaMode mode) {
        if (score >= 80) return "Solid technical stack and coherent implementation bounds.";
        if (score >= 60) return "Plausible tech stack; requires defined architectural layers and API contracts.";
        return "Feasibility undefined; technical specifications and libraries must be documented.";
    }

    private String getImpactExplanation(int score, IdeaMode mode) {
        if (score >= 80) return "Demonstrates clear user reach and scalable societal or business impact.";
        if (score >= 60) return "Good potential audience with targeted utility.";
        return "Target beneficiaries and measurable outcome impact need clear definition.";
    }

    private String getMarketExplanation(int score, IdeaMode mode) {
        if (score >= 80) return "Well-articulated pain point and verified problem formulation.";
        if (score >= 60) return "Reasonable problem statement; can be enriched with real customer scenarios.";
        return "Problem context is generic; define exact user frictions.";
    }

    private String getReadinessExplanation(int score, String stage) {
        return "Current stage: " + stage + " with " + score + "% structural completeness.";
    }

    private String getEvidenceExplanation(int score, Idea idea) {
        int exps = (idea.getId() != null && experimentRepository != null) ?
                experimentRepository.findByIdeaIdOrderByCreatedAtDesc(idea.getId()).size() :
                (idea.getExperiments() != null ? idea.getExperiments().size() : 0);
        int fb = (idea.getId() != null && feedbackRepository != null) ?
                feedbackRepository.findByIdeaIdOrderByCreatedAtDesc(idea.getId()).size() :
                (idea.getFeedbacks() != null ? idea.getFeedbacks().size() : 0);
        return "Supported by " + exps + " validation experiment(s) and " + fb + " peer feedback submission(s).";
    }

    public ScoreBreakdownResponse toResponse(Assessment assessment) {
        ScoreBreakdownResponse res = new ScoreBreakdownResponse();
        res.setAssessmentId(assessment.getId());
        res.setIdeaId(assessment.getIdea() != null ? assessment.getIdea().getId() : null);
        res.setMode(assessment.getMode());
        res.setOverallScore(assessment.getOverallScore());
        res.setInnovationScore(assessment.getInnovationScore());
        res.setFeasibilityScore(assessment.getFeasibilityScore());
        res.setImpactScore(assessment.getImpactScore());
        res.setMarketDemandScore(assessment.getMarketDemandScore());
        res.setReadinessScore(assessment.getReadinessScore());
        res.setEvidenceScore(assessment.getEvidenceScore());
        res.setScoringVersion(assessment.getScoringVersion());

        try {
            if (StringUtils.hasText(assessment.getCriterionBreakdownJson())) {
                Map<String, CriterionDetail> criteria = objectMapper.readValue(
                        assessment.getCriterionBreakdownJson(),
                        new TypeReference<Map<String, CriterionDetail>>() {}
                );
                res.setCriteria(criteria);
            }
            if (StringUtils.hasText(assessment.getStrengthsJson())) {
                res.setStrengths(objectMapper.readValue(assessment.getStrengthsJson(), new TypeReference<List<String>>() {}));
            }
            if (StringUtils.hasText(assessment.getWeaknessesJson())) {
                res.setWeaknesses(objectMapper.readValue(assessment.getWeaknessesJson(), new TypeReference<List<String>>() {}));
            }
            if (StringUtils.hasText(assessment.getRecommendationsJson())) {
                res.setRecommendations(objectMapper.readValue(assessment.getRecommendationsJson(), new TypeReference<List<String>>() {}));
            }
        } catch (Exception e) {
            res.setStrengths(Collections.emptyList());
            res.setWeaknesses(Collections.emptyList());
            res.setRecommendations(Collections.emptyList());
        }

        return res;
    }
}
