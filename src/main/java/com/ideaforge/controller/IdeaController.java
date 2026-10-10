package com.ideaforge.controller;

import com.ideaforge.dto.IdeaRequest;
import com.ideaforge.dto.IdeaResponse;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.User;
import com.ideaforge.service.AuthService;
import com.ideaforge.service.IdeaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ideas")
public class IdeaController {

    private final IdeaService ideaService;
    private final AuthService authService;

    public IdeaController(IdeaService ideaService, AuthService authService) {
        this.ideaService = ideaService;
        this.authService = authService;
    }

    @PostMapping
    public ResponseEntity<IdeaResponse> createIdea(@Valid @RequestBody IdeaRequest request,
                                                  Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        IdeaResponse response = ideaService.createIdea(request, currentUser);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<IdeaResponse>> getAllIdeas(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) IdeaMode mode) {
        List<IdeaResponse> ideas = ideaService.getAllIdeas(query, mode);
        return ResponseEntity.ok(ideas);
    }

    @GetMapping("/my")
    public ResponseEntity<List<IdeaResponse>> getMyIdeas(Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        List<IdeaResponse> myIdeas = ideaService.getMyIdeas(currentUser);
        return ResponseEntity.ok(myIdeas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<IdeaResponse> getIdeaById(@PathVariable Long id) {
        IdeaResponse response = ideaService.getIdeaById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<IdeaResponse> updateIdea(@PathVariable Long id,
                                                   @Valid @RequestBody IdeaRequest request,
                                                   Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        IdeaResponse response = ideaService.updateIdea(id, request, currentUser);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIdea(@PathVariable Long id, Authentication authentication) {
        User currentUser = authService.getUserByEmail(authentication.getName());
        ideaService.deleteIdea(id, currentUser);
        return ResponseEntity.noContent().build();
    }
}
