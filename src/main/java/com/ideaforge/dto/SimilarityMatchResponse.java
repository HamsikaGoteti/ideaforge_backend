package com.ideaforge.dto;

import com.ideaforge.entity.IdeaMode;
import java.util.List;

public class SimilarityMatchResponse {
    private Long id;
    private String title;
    private String tagline;
    private IdeaMode mode;
    private String currentStage;
    private int matchPercentage;
    private List<String> commonKeywords;
    private String matchReason;
    private Long ownerId;
    private String ownerName;

    public SimilarityMatchResponse() {}

    public SimilarityMatchResponse(Long id, String title, String tagline, IdeaMode mode, String currentStage,
                                   int matchPercentage, List<String> commonKeywords, String matchReason,
                                   Long ownerId, String ownerName) {
        this.id = id;
        this.title = title;
        this.tagline = tagline;
        this.mode = mode;
        this.currentStage = currentStage;
        this.matchPercentage = matchPercentage;
        this.commonKeywords = commonKeywords;
        this.matchReason = matchReason;
        this.ownerId = ownerId;
        this.ownerName = ownerName;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    public IdeaMode getMode() { return mode; }
    public void setMode(IdeaMode mode) { this.mode = mode; }
    public String getCurrentStage() { return currentStage; }
    public void setCurrentStage(String currentStage) { this.currentStage = currentStage; }
    public int getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(int matchPercentage) { this.matchPercentage = matchPercentage; }
    public List<String> getCommonKeywords() { return commonKeywords; }
    public void setCommonKeywords(List<String> commonKeywords) { this.commonKeywords = commonKeywords; }
    public String getMatchReason() { return matchReason; }
    public void setMatchReason(String matchReason) { this.matchReason = matchReason; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
}
