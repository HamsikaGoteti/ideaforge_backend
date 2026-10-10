package com.ideaforge.dto;

import com.ideaforge.entity.IdeaMode;
import java.util.List;
import java.util.Map;

public class ScoreBreakdownResponse {
    private Long assessmentId;
    private Long ideaId;
    private IdeaMode mode;
    private int overallScore;
    private int innovationScore;
    private int feasibilityScore;
    private int impactScore;
    private int marketDemandScore;
    private int readinessScore;
    private int evidenceScore;
    private String scoringVersion;
    private Map<String, CriterionDetail> criteria;
    private List<String> strengths;
    private List<String> weaknesses;
    private List<String> recommendations;

    public ScoreBreakdownResponse() {}

    public static class CriterionDetail {
        private String name;
        private int score;
        private double weight;
        private String explanation;

        public CriterionDetail() {}

        public CriterionDetail(String name, int score, double weight, String explanation) {
            this.name = name;
            this.score = score;
            this.weight = weight;
            this.explanation = explanation;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public int getScore() { return score; }
        public void setScore(int score) { this.score = score; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }

    // Getters and Setters
    public Long getAssessmentId() { return assessmentId; }
    public void setAssessmentId(Long assessmentId) { this.assessmentId = assessmentId; }
    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }
    public IdeaMode getMode() { return mode; }
    public void setMode(IdeaMode mode) { this.mode = mode; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public int getInnovationScore() { return innovationScore; }
    public void setInnovationScore(int innovationScore) { this.innovationScore = innovationScore; }
    public int getFeasibilityScore() { return feasibilityScore; }
    public void setFeasibilityScore(int feasibilityScore) { this.feasibilityScore = feasibilityScore; }
    public int getImpactScore() { return impactScore; }
    public void setImpactScore(int impactScore) { this.impactScore = impactScore; }
    public int getMarketDemandScore() { return marketDemandScore; }
    public void setMarketDemandScore(int marketDemandScore) { this.marketDemandScore = marketDemandScore; }
    public int getReadinessScore() { return readinessScore; }
    public void setReadinessScore(int readinessScore) { this.readinessScore = readinessScore; }
    public int getEvidenceScore() { return evidenceScore; }
    public void setEvidenceScore(int evidenceScore) { this.evidenceScore = evidenceScore; }
    public String getScoringVersion() { return scoringVersion; }
    public void setScoringVersion(String scoringVersion) { this.scoringVersion = scoringVersion; }
    public Map<String, CriterionDetail> getCriteria() { return criteria; }
    public void setCriteria(Map<String, CriterionDetail> criteria) { this.criteria = criteria; }
    public List<String> getStrengths() { return strengths; }
    public void setStrengths(List<String> strengths) { this.strengths = strengths; }
    public List<String> getWeaknesses() { return weaknesses; }
    public void setWeaknesses(List<String> weaknesses) { this.weaknesses = weaknesses; }
    public List<String> getRecommendations() { return recommendations; }
    public void setRecommendations(List<String> recommendations) { this.recommendations = recommendations; }
}
