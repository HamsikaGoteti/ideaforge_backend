package com.ideaforge.controller;

import com.ideaforge.dto.FeedbackRequest;
import com.ideaforge.dto.FeedbackResponse;
import com.ideaforge.entity.User;
import com.ideaforge.service.AuthService;
import com.ideaforge.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FeedbackController {

    private final FeedbackService feedbackService;
    private final AuthService authService;

    public FeedbackController(FeedbackService feedbackService, AuthService authService) {
        this.feedbackService = feedbackService;
        this.authService = authService;
    }

    @PostMapping("/ideas/{ideaId}/feedback")
    public ResponseEntity<FeedbackResponse> addFeedback(
            @PathVariable Long ideaId,
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        FeedbackResponse response = feedbackService.addFeedback(ideaId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ideas/{ideaId}/feedback")
    public ResponseEntity<List<FeedbackResponse>> getFeedbackForIdea(@PathVariable Long ideaId) {
        List<FeedbackResponse> feedback = feedbackService.getFeedbackForIdea(ideaId);
        return ResponseEntity.ok(feedback);
    }

    @PutMapping("/feedback/{feedbackId}")
    public ResponseEntity<FeedbackResponse> updateFeedback(
            @PathVariable Long feedbackId,
            @Valid @RequestBody FeedbackRequest request,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        FeedbackResponse response = feedbackService.updateFeedback(feedbackId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/feedback/{feedbackId}")
    public ResponseEntity<Void> deleteFeedback(
            @PathVariable Long feedbackId,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        feedbackService.deleteFeedback(feedbackId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
