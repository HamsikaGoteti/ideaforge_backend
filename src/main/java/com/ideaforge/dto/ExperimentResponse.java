package com.ideaforge.dto;

import com.ideaforge.entity.Experiment;
import com.ideaforge.entity.ExperimentStatus;
import java.time.LocalDateTime;

public class ExperimentResponse {
    private Long id;
    private Long ideaId;
    private Long userId;
    private String userName;
    private String title;
    private String hypothesis;
    private String objective;
    private String steps;
    private String successCriteria;
    private String observations;
    private String results;
    private String evidenceLinks;
    private String lessonsLearned;
    private String followUpActions;
    private ExperimentStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ExperimentResponse() {}

    public static ExperimentResponse fromEntity(Experiment exp) {
        ExperimentResponse res = new ExperimentResponse();
        res.setId(exp.getId());
        if (exp.getIdea() != null) {
            res.setIdeaId(exp.getIdea().getId());
        }
        if (exp.getUser() != null) {
            res.setUserId(exp.getUser().getId());
            res.setUserName(exp.getUser().getName());
        }
        res.setTitle(exp.getTitle());
        res.setHypothesis(exp.getHypothesis());
        res.setObjective(exp.getObjective());
        res.setSteps(exp.getSteps());
        res.setSuccessCriteria(exp.getSuccessCriteria());
        res.setObservations(exp.getObservations());
        res.setResults(exp.getResults());
        res.setEvidenceLinks(exp.getEvidenceLinks());
        res.setLessonsLearned(exp.getLessonsLearned());
        res.setFollowUpActions(exp.getFollowUpActions());
        res.setStatus(exp.getStatus());
        res.setCreatedAt(exp.getCreatedAt());
        res.setUpdatedAt(exp.getUpdatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
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
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
