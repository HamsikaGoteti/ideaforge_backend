package com.ideaforge.dto;

import com.ideaforge.entity.Feedback;
import com.ideaforge.entity.FeedbackCategory;
import java.time.LocalDateTime;

public class FeedbackResponse {
    private Long id;
    private Long ideaId;
    private Long authorId;
    private String authorName;
    private FeedbackCategory category;
    private String content;
    private int rating;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FeedbackResponse() {}

    public static FeedbackResponse fromEntity(Feedback feedback) {
        FeedbackResponse res = new FeedbackResponse();
        res.setId(feedback.getId());
        if (feedback.getIdea() != null) {
            res.setIdeaId(feedback.getIdea().getId());
        }
        if (feedback.getUser() != null) {
            res.setAuthorId(feedback.getUser().getId());
            res.setAuthorName(feedback.getUser().getName());
        }
        res.setCategory(feedback.getCategory());
        res.setContent(feedback.getContent());
        res.setRating(feedback.getRating());
        res.setCreatedAt(feedback.getCreatedAt());
        res.setUpdatedAt(feedback.getUpdatedAt());
        return res;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getIdeaId() { return ideaId; }
    public void setIdeaId(Long ideaId) { this.ideaId = ideaId; }
    public Long getAuthorId() { return authorId; }
    public void setAuthorId(Long authorId) { this.authorId = authorId; }
    public String getAuthorName() { return authorName; }
    public void setAuthorName(String authorName) { this.authorName = authorName; }
    public FeedbackCategory getCategory() { return category; }
    public void setCategory(FeedbackCategory category) { this.category = category; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
