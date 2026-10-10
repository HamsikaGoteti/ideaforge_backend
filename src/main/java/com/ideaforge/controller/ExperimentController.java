package com.ideaforge.controller;

import com.ideaforge.dto.ExperimentRequest;
import com.ideaforge.dto.ExperimentResponse;
import com.ideaforge.entity.User;
import com.ideaforge.service.AuthService;
import com.ideaforge.service.ExperimentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ExperimentController {

    private final ExperimentService experimentService;
    private final AuthService authService;

    public ExperimentController(ExperimentService experimentService, AuthService authService) {
        this.experimentService = experimentService;
        this.authService = authService;
    }

    @PostMapping("/ideas/{ideaId}/experiments")
    public ResponseEntity<ExperimentResponse> createExperiment(
            @PathVariable Long ideaId,
            @Valid @RequestBody ExperimentRequest request,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        ExperimentResponse response = experimentService.createExperiment(ideaId, request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/ideas/{ideaId}/experiments")
    public ResponseEntity<List<ExperimentResponse>> getExperimentsForIdea(@PathVariable Long ideaId) {
        List<ExperimentResponse> list = experimentService.getExperimentsForIdea(ideaId);
        return ResponseEntity.ok(list);
    }

    @GetMapping("/experiments/{expId}")
    public ResponseEntity<ExperimentResponse> getExperimentById(@PathVariable Long expId) {
        ExperimentResponse response = experimentService.getExperimentById(expId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/experiments/{expId}")
    public ResponseEntity<ExperimentResponse> updateExperiment(
            @PathVariable Long expId,
            @Valid @RequestBody ExperimentRequest request,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        ExperimentResponse response = experimentService.updateExperiment(expId, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/experiments/{expId}")
    public ResponseEntity<Void> deleteExperiment(
            @PathVariable Long expId,
            Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        experimentService.deleteExperiment(expId, currentUser);
        return ResponseEntity.noContent().build();
    }
}
