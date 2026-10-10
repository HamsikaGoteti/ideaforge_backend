package com.ideaforge.service;

import com.ideaforge.dto.IdeaRequest;
import com.ideaforge.dto.IdeaResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.BadRequestException;
import com.ideaforge.exception.ResourceNotFoundException;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.IdeaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class IdeaService {

    private final IdeaRepository ideaRepository;
    private final ScoringService scoringService;

    public IdeaService(IdeaRepository ideaRepository, ScoringService scoringService) {
        this.ideaRepository = ideaRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public IdeaResponse createIdea(IdeaRequest req, User currentUser) {
        Idea idea = new Idea();
        mapRequestToEntity(req, idea);
        idea.setUser(currentUser);

        Idea saved = ideaRepository.save(idea);

        // Run initial transparent scoring
        scoringService.evaluateAndSave(saved);

        return IdeaResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<IdeaResponse> getAllIdeas(String query, IdeaMode mode) {
        List<Idea> ideas;
        if (StringUtils.hasText(query)) {
            ideas = ideaRepository.searchIdeas(query.trim());
            if (mode != null) {
                ideas = ideas.stream().filter(i -> i.getMode() == mode).collect(Collectors.toList());
            }
        } else if (mode != null) {
            ideas = ideaRepository.findByModeOrderByCreatedAtDesc(mode);
        } else {
            ideas = ideaRepository.findAllByOrderByCreatedAtDesc();
        }
        return ideas.stream().map(IdeaResponse::fromEntity).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<IdeaResponse> getMyIdeas(User currentUser) {
        return ideaRepository.findByUserIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(IdeaResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Idea getIdeaEntity(Long id) {
        return ideaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public IdeaResponse getIdeaById(Long id) {
        Idea idea = getIdeaEntity(id);
        return IdeaResponse.fromEntity(idea);
    }

    @Transactional
    public IdeaResponse updateIdea(Long id, IdeaRequest req, User currentUser) {
        Idea idea = getIdeaEntity(id);
        checkOwnership(idea, currentUser);

        mapRequestToEntity(req, idea);
        Idea updated = ideaRepository.save(idea);

        // Re-score upon update
        scoringService.evaluateAndSave(updated);

        return IdeaResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteIdea(Long id, User currentUser) {
        Idea idea = getIdeaEntity(id);
        checkOwnership(idea, currentUser);
        ideaRepository.delete(idea);
    }

    public void checkOwnership(Idea idea, User user) {
        if (user.getRole() == Role.ROLE_ADMIN) {
            return;
        }
        if (!idea.getUser().getId().equals(user.getId())) {
            throw new UnauthorizedException("You do not have permission to modify this idea.");
        }
    }

    private void mapRequestToEntity(IdeaRequest req, Idea idea) {
        idea.setTitle(req.getTitle().trim());
        idea.setTagline(req.getTagline() != null ? req.getTagline().trim() : null);
        idea.setDescription(req.getDescription().trim());
        idea.setMode(req.getMode());
        idea.setTargetAudience(req.getTargetAudience());
        idea.setProblemStatement(req.getProblemStatement());
        idea.setProposedSolution(req.getProposedSolution());
        idea.setKeyFeatures(req.getKeyFeatures());
        idea.setTechStack(req.getTechStack());
        if (StringUtils.hasText(req.getCurrentStage())) {
            idea.setCurrentStage(req.getCurrentStage().trim());
        }
        idea.setTags(req.getTags());
    }
}
