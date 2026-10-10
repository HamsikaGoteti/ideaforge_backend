package com.ideaforge.controller;

import com.ideaforge.dto.ScoreBreakdownResponse;
import com.ideaforge.entity.Assessment;
import com.ideaforge.entity.Idea;
import com.ideaforge.repository.AssessmentRepository;
import com.ideaforge.service.IdeaService;
import com.ideaforge.service.ScoringService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("/api/ideas/{id}")
@Transactional
public class ScoringController {

    private final ScoringService scoringService;
    private final IdeaService ideaService;
    private final AssessmentRepository assessmentRepository;

    public ScoringController(ScoringService scoringService,
                             IdeaService ideaService,
                             AssessmentRepository assessmentRepository) {
        this.scoringService = scoringService;
        this.ideaService = ideaService;
        this.assessmentRepository = assessmentRepository;
    }

    @GetMapping("/score")
    public ResponseEntity<ScoreBreakdownResponse> getScore(@PathVariable Long id) {
        Idea idea = ideaService.getIdeaEntity(id);
        Assessment assessment = assessmentRepository.findFirstByIdeaIdOrderByCreatedAtDesc(id)
                .orElseGet(() -> scoringService.evaluateAndSave(idea));

        return ResponseEntity.ok(scoringService.toResponse(assessment));
    }

    @PostMapping("/reassess")
    public ResponseEntity<ScoreBreakdownResponse> reassessScore(@PathVariable Long id) {
        Idea idea = ideaService.getIdeaEntity(id);
        Assessment assessment = scoringService.evaluateAndSave(idea);
        return ResponseEntity.ok(scoringService.toResponse(assessment));
    }
}
