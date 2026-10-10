package com.ideaforge.service;

import com.ideaforge.dto.FeedbackRequest;
import com.ideaforge.dto.FeedbackResponse;
import com.ideaforge.entity.Feedback;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.ResourceNotFoundException;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.FeedbackRepository;
import com.ideaforge.repository.IdeaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final IdeaRepository ideaRepository;
    private final ScoringService scoringService;

    public FeedbackService(FeedbackRepository feedbackRepository,
                           IdeaRepository ideaRepository,
                           ScoringService scoringService) {
        this.feedbackRepository = feedbackRepository;
        this.ideaRepository = ideaRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public FeedbackResponse addFeedback(Long ideaId, FeedbackRequest req, User currentUser) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found with id: " + ideaId));

        Feedback feedback = new Feedback();
        feedback.setIdea(idea);
        feedback.setUser(currentUser);
        feedback.setCategory(req.getCategory());
        feedback.setContent(req.getContent().trim());
        feedback.setRating(req.getRating());

        Feedback saved = feedbackRepository.save(feedback);

        // Feedback contributes to validation evidence, reassess score
        scoringService.evaluateAndSave(idea);

        return FeedbackResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbackForIdea(Long ideaId) {
        return feedbackRepository.findByIdeaIdOrderByCreatedAtDesc(ideaId)
                .stream()
                .map(FeedbackResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public FeedbackResponse updateFeedback(Long feedbackId, FeedbackRequest req, User currentUser) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        if (!feedback.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You can only edit your own feedback.");
        }

        feedback.setCategory(req.getCategory());
        feedback.setContent(req.getContent().trim());
        feedback.setRating(req.getRating());

        Feedback updated = feedbackRepository.save(feedback);
        scoringService.evaluateAndSave(feedback.getIdea());
        return FeedbackResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteFeedback(Long feedbackId, User currentUser) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback not found with id: " + feedbackId));

        boolean isAuthor = feedback.getUser().getId().equals(currentUser.getId());
        boolean isIdeaOwner = feedback.getIdea().getUser().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ROLE_ADMIN;

        if (!isAuthor && !isIdeaOwner && !isAdmin) {
            throw new UnauthorizedException("You do not have permission to delete this feedback.");
        }

        Idea idea = feedback.getIdea();
        feedbackRepository.delete(feedback);

        // Reassess idea evidence score
        scoringService.evaluateAndSave(idea);
    }
}
