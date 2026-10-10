package com.ideaforge.dto;

import com.ideaforge.entity.IdeaMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class IdeaRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @Size(max = 300, message = "Tagline cannot exceed 300 characters")
    private String tagline;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Mode is required")
    private IdeaMode mode;

    @Size(max = 255, message = "Target audience cannot exceed 255 characters")
    private String targetAudience;

    private String problemStatement;
    private String proposedSolution;
    private String keyFeatures;

    @Size(max = 255, message = "Tech stack cannot exceed 255 characters")
    private String techStack;

    @Size(max = 50, message = "Stage cannot exceed 50 characters")
    private String currentStage;

    @Size(max = 255, message = "Tags cannot exceed 255 characters")
    private String tags;

    public IdeaRequest() {}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public IdeaMode getMode() {
        return mode;
    }

    public void setMode(IdeaMode mode) {
        this.mode = mode;
    }

    public String getTargetAudience() {
        return targetAudience;
    }

    public void setTargetAudience(String targetAudience) {
        this.targetAudience = targetAudience;
    }

    public String getProblemStatement() {
        return problemStatement;
    }

    public void setProblemStatement(String problemStatement) {
        this.problemStatement = problemStatement;
    }

    public String getProposedSolution() {
        return proposedSolution;
    }

    public void setProposedSolution(String proposedSolution) {
        this.proposedSolution = proposedSolution;
    }

    public String getKeyFeatures() {
        return keyFeatures;
    }

    public void setKeyFeatures(String keyFeatures) {
        this.keyFeatures = keyFeatures;
    }

    public String getTechStack() {
        return techStack;
    }

    public void setTechStack(String techStack) {
        this.techStack = techStack;
    }

    public String getCurrentStage() {
        return currentStage;
    }

    public void setCurrentStage(String currentStage) {
        this.currentStage = currentStage;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }
}
