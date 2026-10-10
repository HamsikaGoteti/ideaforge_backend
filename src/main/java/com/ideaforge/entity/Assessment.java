package com.ideaforge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    @JsonIgnoreProperties({"assessments", "user", "hibernateLazyInitializer", "handler"})
    private Idea idea;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private IdeaMode mode;

    @Column(nullable = false)
    private int overallScore;

    @Column(nullable = false)
    private int innovationScore;

    @Column(nullable = false)
    private int feasibilityScore;

    @Column(nullable = false)
    private int impactScore;

    @Column(nullable = false)
    private int marketDemandScore;

    @Column(nullable = false)
    private int readinessScore;

    @Column(nullable = false)
    private int evidenceScore;

    @Column(nullable = false, length = 50)
    private String scoringVersion = "v1.0-deterministic";

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String criterionBreakdownJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String strengthsJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String weaknessesJson;

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    private String recommendationsJson;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Assessment() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.scoringVersion == null) {
            this.scoringVersion = "v1.0-deterministic";
        }
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Idea getIdea() {
        return idea;
    }

    public void setIdea(Idea idea) {
        this.idea = idea;
    }

    public IdeaMode getMode() {
        return mode;
    }

    public void setMode(IdeaMode mode) {
        this.mode = mode;
    }

    public int getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(int overallScore) {
        this.overallScore = overallScore;
    }

    public int getInnovationScore() {
        return innovationScore;
    }

    public void setInnovationScore(int innovationScore) {
        this.innovationScore = innovationScore;
    }

    public int getFeasibilityScore() {
        return feasibilityScore;
    }

    public void setFeasibilityScore(int feasibilityScore) {
        this.feasibilityScore = feasibilityScore;
    }

    public int getImpactScore() {
        return impactScore;
    }

    public void setImpactScore(int impactScore) {
        this.impactScore = impactScore;
    }

    public int getMarketDemandScore() {
        return marketDemandScore;
    }

    public void setMarketDemandScore(int marketDemandScore) {
        this.marketDemandScore = marketDemandScore;
    }

    public int getReadinessScore() {
        return readinessScore;
    }

    public void setReadinessScore(int readinessScore) {
        this.readinessScore = readinessScore;
    }

    public int getEvidenceScore() {
        return evidenceScore;
    }

    public void setEvidenceScore(int evidenceScore) {
        this.evidenceScore = evidenceScore;
    }

    public String getScoringVersion() {
        return scoringVersion;
    }

    public void setScoringVersion(String scoringVersion) {
        this.scoringVersion = scoringVersion;
    }

    public String getCriterionBreakdownJson() {
        return criterionBreakdownJson;
    }

    public void setCriterionBreakdownJson(String criterionBreakdownJson) {
        this.criterionBreakdownJson = criterionBreakdownJson;
    }

    public String getStrengthsJson() {
        return strengthsJson;
    }

    public void setStrengthsJson(String strengthsJson) {
        this.strengthsJson = strengthsJson;
    }

    public String getWeaknessesJson() {
        return weaknessesJson;
    }

    public void setWeaknessesJson(String weaknessesJson) {
        this.weaknessesJson = weaknessesJson;
    }

    public String getRecommendationsJson() {
        return recommendationsJson;
    }

    public void setRecommendationsJson(String recommendationsJson) {
        this.recommendationsJson = recommendationsJson;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
