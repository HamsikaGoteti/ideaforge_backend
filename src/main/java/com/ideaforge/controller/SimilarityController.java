package com.ideaforge.controller;

import com.ideaforge.dto.SimilarityMatchResponse;
import com.ideaforge.service.SimilarityService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas/{id}/similar")
public class SimilarityController {

    private final SimilarityService similarityService;

    public SimilarityController(SimilarityService similarityService) {
        this.similarityService = similarityService;
    }

    @GetMapping
    public ResponseEntity<List<SimilarityMatchResponse>> getSimilarIdeas(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "15") Integer minThreshold) {
        List<SimilarityMatchResponse> matches = similarityService.findSimilarIdeas(id, minThreshold);
        return ResponseEntity.ok(matches);
    }
}
