package com.ideaforge;

import com.ideaforge.dto.FeedbackRequest;
import com.ideaforge.dto.FeedbackResponse;
import com.ideaforge.dto.IdeaRequest;
import com.ideaforge.dto.IdeaResponse;
import com.ideaforge.entity.*;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.FeedbackRepository;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.FeedbackService;
import com.ideaforge.service.IdeaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class FeedbackServiceTest {

    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    private User ideaOwner;
    private User reviewer;
    private User otherUser;
    private IdeaResponse idea;

    @BeforeEach
    void setUp() {
        feedbackRepository.deleteAll();
        ideaRepository.deleteAll();
        userRepository.deleteAll();

        ideaOwner = userRepository.save(new User("Idea Owner", "owner@example.com", "pass", Role.ROLE_USER));
        reviewer = userRepository.save(new User("Reviewer", "reviewer@example.com", "pass", Role.ROLE_USER));
        otherUser = userRepository.save(new User("Other", "other@example.com", "pass", Role.ROLE_USER));

        IdeaRequest req = new IdeaRequest();
        req.setTitle("Smart Grid AI");
        req.setDescription("Autonomous renewable energy load balancer");
        req.setMode(IdeaMode.STARTUP);
        idea = ideaService.createIdea(req, ideaOwner);
    }

    @Test
    void testAddAndRetrieveFeedback() {
        FeedbackRequest req = new FeedbackRequest();
        req.setCategory(FeedbackCategory.STRENGTHS);
        req.setContent("Very strong market potential and technical feasibility.");
        req.setRating(5);

        FeedbackResponse res = feedbackService.addFeedback(idea.getId(), req, reviewer);
        assertNotNull(res.getId());
        assertEquals(FeedbackCategory.STRENGTHS, res.getCategory());
        assertEquals("Very strong market potential and technical feasibility.", res.getContent());
        assertEquals(5, res.getRating());

        List<FeedbackResponse> list = feedbackService.getFeedbackForIdea(idea.getId());
        assertEquals(1, list.size());
    }

    @Test
    void testUserCanOnlyEditOwnFeedback() {
        FeedbackRequest req = new FeedbackRequest();
        req.setCategory(FeedbackCategory.CONCERNS);
        req.setContent("Initial security audit required.");
        req.setRating(3);

        FeedbackResponse created = feedbackService.addFeedback(idea.getId(), req, reviewer);

        FeedbackRequest updateReq = new FeedbackRequest();
        updateReq.setCategory(FeedbackCategory.SUGGESTIONS);
        updateReq.setContent("Unauthorized modification attempt.");
        updateReq.setRating(1);

        assertThrows(UnauthorizedException.class, () ->
                feedbackService.updateFeedback(created.getId(), updateReq, otherUser));
    }
}
