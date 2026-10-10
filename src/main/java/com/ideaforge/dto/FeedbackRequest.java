package com.ideaforge.dto;

import com.ideaforge.entity.Feedback;
import com.ideaforge.entity.FeedbackCategory;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class FeedbackRequest {

    @NotNull(message = "Category is required")
    private FeedbackCategory category;

    @NotBlank(message = "Content is required")
    private String content;

    @Min(value = 1, message = "Rating must be between 1 and 5")
    @Max(value = 5, message = "Rating must be between 1 and 5")
    private int rating = 5;

    public FeedbackRequest() {}

    public FeedbackCategory getCategory() { return category; }
    public void setCategory(FeedbackCategory category) { this.category = category; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
}
