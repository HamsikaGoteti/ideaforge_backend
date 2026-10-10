package com.ideaforge.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "experiments")
public class Experiment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idea_id", nullable = false)
    @JsonIgnoreProperties({"experiments", "feedbacks", "assessments", "user", "hibernateLazyInitializer", "handler"})
    private Idea idea;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties({"password", "hibernateLazyInitializer", "handler"})
    private User user;

    @NotBlank
    @Size(max = 200)
    @Column(nullable = false, length = 200)
    private String title;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String hypothesis;

    @NotBlank
    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String objective;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String steps;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String successCriteria;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String observations;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String results;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String evidenceLinks;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String lessonsLearned;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String followUpActions;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExperimentStatus status = ExperimentStatus.PLANNED;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public Experiment() {
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ExperimentStatus.PLANNED;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
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

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHypothesis() {
        return hypothesis;
    }

    public void setHypothesis(String hypothesis) {
        this.hypothesis = hypothesis;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
    }

    public String getSuccessCriteria() {
        return successCriteria;
    }

    public void setSuccessCriteria(String successCriteria) {
        this.successCriteria = successCriteria;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getResults() {
        return results;
    }

    public void setResults(String results) {
        this.results = results;
    }

    public String getEvidenceLinks() {
        return evidenceLinks;
    }

    public void setEvidenceLinks(String evidenceLinks) {
        this.evidenceLinks = evidenceLinks;
    }

    public String getLessonsLearned() {
        return lessonsLearned;
    }

    public void setLessonsLearned(String lessonsLearned) {
        this.lessonsLearned = lessonsLearned;
    }

    public String getFollowUpActions() {
        return followUpActions;
    }

    public void setFollowUpActions(String followUpActions) {
        this.followUpActions = followUpActions;
    }

    public ExperimentStatus getStatus() {
        return status;
    }

    public void setStatus(ExperimentStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
