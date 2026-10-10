package com.ideaforge.dto;

import com.ideaforge.entity.ExperimentStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ExperimentRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;

    @NotBlank(message = "Hypothesis is required")
    private String hypothesis;

    @NotBlank(message = "Objective is required")
    private String objective;

    private String steps;
    private String successCriteria;
    private String observations;
    private String results;
    private String evidenceLinks;
    private String lessonsLearned;
    private String followUpActions;

    @NotNull(message = "Status is required")
    private ExperimentStatus status = ExperimentStatus.PLANNED;

    public ExperimentRequest() {}

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getHypothesis() { return hypothesis; }
    public void setHypothesis(String hypothesis) { this.hypothesis = hypothesis; }
    public String getObjective() { return objective; }
    public void setObjective(String objective) { this.objective = objective; }
    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }
    public String getObservations() { return observations; }
    public void setObservations(String observations) { this.observations = observations; }
    public String getResults() { return results; }
    public void setResults(String results) { this.results = results; }
    public String getEvidenceLinks() { return evidenceLinks; }
    public void setEvidenceLinks(String evidenceLinks) { this.evidenceLinks = evidenceLinks; }
    public String getLessonsLearned() { return lessonsLearned; }
    public void setLessonsLearned(String lessonsLearned) { this.lessonsLearned = lessonsLearned; }
    public String getFollowUpActions() { return followUpActions; }
    public void setFollowUpActions(String followUpActions) { this.followUpActions = followUpActions; }
    public ExperimentStatus getStatus() { return status; }
    public void setStatus(ExperimentStatus status) { this.status = status; }
}
