package com.ideaforge.dto;

import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import java.time.LocalDateTime;

public class IdeaResponse {
    private Long id;
    private String title;
    private String tagline;
    private String description;
    private IdeaMode mode;
    private String targetAudience;
    private String problemStatement;
    private String proposedSolution;
    private String keyFeatures;
    private String techStack;
    private String currentStage;
    private String tags;
    private String blueprintJson;
    private String blueprintSource;
    private int overallScore;
    private Long userId;
    private String userName;
    private int assessmentCount;
    private int feedbackCount;
    private int experimentCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IdeaResponse() {}

    public static IdeaResponse fromEntity(Idea idea) {
        IdeaResponse res = new IdeaResponse();
        res.setId(idea.getId());
        res.setTitle(idea.getTitle());
        res.setTagline(idea.getTagline());
        res.setDescription(idea.getDescription());
        res.setMode(idea.getMode());
        res.setTargetAudience(idea.getTargetAudience());
        res.setProblemStatement(idea.getProblemStatement());
        res.setProposedSolution(idea.getProposedSolution());
        res.setKeyFeatures(idea.getKeyFeatures());
        res.setTechStack(idea.getTechStack());
        res.setCurrentStage(idea.getCurrentStage());
        res.setTags(idea.getTags());
        res.setBlueprintJson(idea.getBlueprintJson());
        res.setBlueprintSource(idea.getBlueprintSource());
        res.setOverallScore(idea.getOverallScore());
        if (idea.getUser() != null) {
            res.setUserId(idea.getUser().getId());
            res.setUserName(idea.getUser().getName());
        }
        res.setAssessmentCount(idea.getAssessments() != null ? idea.getAssessments().size() : 0);
        res.setFeedbackCount(idea.getFeedbacks() != null ? idea.getFeedbacks().size() : 0);
        res.setExperimentCount(idea.getExperiments() != null ? idea.getExperiments().size() : 0);
        res.setCreatedAt(idea.getCreatedAt());
        res.setUpdatedAt(idea.getUpdatedAt());
        return res;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public IdeaMode getMode() { return mode; }
    public void setMode(IdeaMode mode) { this.mode = mode; }
    public String getTargetAudience() { return targetAudience; }
    public void setTargetAudience(String targetAudience) { this.targetAudience = targetAudience; }
    public String getProblemStatement() { return problemStatement; }
    public void setProblemStatement(String problemStatement) { this.problemStatement = problemStatement; }
    public String getProposedSolution() { return proposedSolution; }
    public void setProposedSolution(String proposedSolution) { this.proposedSolution = proposedSolution; }
    public String getKeyFeatures() { return keyFeatures; }
    public void setKeyFeatures(String keyFeatures) { this.keyFeatures = keyFeatures; }
    public String getTechStack() { return techStack; }
    public void setTechStack(String techStack) { this.techStack = techStack; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }
    public String getBlueprintJson() { return blueprintJson; }
    public void setBlueprintJson(String blueprintJson) { this.blueprintJson = blueprintJson; }
    public String getBlueprintSource() { return blueprintSource; }
    public void setBlueprintSource(String blueprintSource) { this.blueprintSource = blueprintSource; }
    public int getOverallScore() { return overallScore; }
    public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public int getAssessmentCount() { return assessmentCount; }
    public void setAssessmentCount(int assessmentCount) { this.assessmentCount = assessmentCount; }
    public int getFeedbackCount() { return feedbackCount; }
    public void setFeedbackCount(int feedbackCount) { this.feedbackCount = feedbackCount; }
    public int getExperimentCount() { return experimentCount; }
    public void setExperimentCount(int experimentCount) { this.experimentCount = experimentCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
