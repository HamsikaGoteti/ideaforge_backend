package com.ideaforge.service;

import com.ideaforge.dto.ExperimentRequest;
import com.ideaforge.dto.ExperimentResponse;
import com.ideaforge.entity.Experiment;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.ResourceNotFoundException;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.ExperimentRepository;
import com.ideaforge.repository.IdeaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExperimentService {

    private final ExperimentRepository experimentRepository;
    private final IdeaRepository ideaRepository;
    private final ScoringService scoringService;

    public ExperimentService(ExperimentRepository experimentRepository,
                             IdeaRepository ideaRepository,
                             ScoringService scoringService) {
        this.experimentRepository = experimentRepository;
        this.ideaRepository = ideaRepository;
        this.scoringService = scoringService;
    }

    @Transactional
    public ExperimentResponse createExperiment(Long ideaId, ExperimentRequest req, User currentUser) {
        Idea idea = ideaRepository.findById(ideaId)
                .orElseThrow(() -> new ResourceNotFoundException("Idea not found with id: " + ideaId));

        // Enforce idea ownership (only idea owner or admin can create experiments for the idea)
        if (!idea.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("Only the owner of the idea can create experiments.");
        }

        Experiment exp = new Experiment();
        exp.setIdea(idea);
        exp.setUser(currentUser);
        mapRequestToEntity(req, exp);

        Experiment saved = experimentRepository.save(exp);

        // Reassess idea evidence score
        scoringService.evaluateAndSave(idea);

        return ExperimentResponse.fromEntity(saved);
    }

    @Transactional(readOnly = true)
    public List<ExperimentResponse> getExperimentsForIdea(Long ideaId) {
        return experimentRepository.findByIdeaIdOrderByCreatedAtDesc(ideaId)
                .stream()
                .map(ExperimentResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ExperimentResponse getExperimentById(Long expId) {
        Experiment exp = experimentRepository.findById(expId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiment not found with id: " + expId));
        return ExperimentResponse.fromEntity(exp);
    }

    @Transactional
    public ExperimentResponse updateExperiment(Long expId, ExperimentRequest req, User currentUser) {
        Experiment exp = experimentRepository.findById(expId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiment not found with id: " + expId));

        if (!exp.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You do not have permission to modify this experiment.");
        }

        mapRequestToEntity(req, exp);
        Experiment updated = experimentRepository.save(exp);

        // Reassess idea evidence score
        scoringService.evaluateAndSave(exp.getIdea());

        return ExperimentResponse.fromEntity(updated);
    }

    @Transactional
    public void deleteExperiment(Long expId, User currentUser) {
        Experiment exp = experimentRepository.findById(expId)
                .orElseThrow(() -> new ResourceNotFoundException("Experiment not found with id: " + expId));

        if (!exp.getUser().getId().equals(currentUser.getId()) && currentUser.getRole() != Role.ROLE_ADMIN) {
            throw new UnauthorizedException("You do not have permission to delete this experiment.");
        }

        Idea idea = exp.getIdea();
        experimentRepository.delete(exp);

        // Reassess idea evidence score
        scoringService.evaluateAndSave(idea);
    }

    private void mapRequestToEntity(ExperimentRequest req, Experiment exp) {
        exp.setTitle(req.getTitle().trim());
        exp.setHypothesis(req.getHypothesis().trim());
        exp.setObjective(req.getObjective().trim());
        exp.setSteps(req.getSteps());
        exp.setSuccessCriteria(req.getSuccessCriteria());
        exp.setObservations(req.getObservations());
        exp.setResults(req.getResults());
        exp.setEvidenceLinks(req.getEvidenceLinks());
        exp.setLessonsLearned(req.getLessonsLearned());
        exp.setFollowUpActions(req.getFollowUpActions());
        exp.setStatus(req.getStatus());
    }
}
