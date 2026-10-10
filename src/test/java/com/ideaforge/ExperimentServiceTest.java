package com.ideaforge;

import com.ideaforge.dto.ExperimentRequest;
import com.ideaforge.dto.ExperimentResponse;
import com.ideaforge.dto.IdeaRequest;
import com.ideaforge.dto.IdeaResponse;
import com.ideaforge.entity.ExperimentStatus;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.ExperimentRepository;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.ExperimentService;
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
class ExperimentServiceTest {

    @Autowired
    private ExperimentService experimentService;

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private ExperimentRepository experimentRepository;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    private User ideaOwner;
    private User otherUser;
    private IdeaResponse idea;

    @BeforeEach
    void setUp() {
        experimentRepository.deleteAll();
        ideaRepository.deleteAll();
        userRepository.deleteAll();

        ideaOwner = userRepository.save(new User("Idea Owner", "owner@example.com", "pass", Role.ROLE_USER));
        otherUser = userRepository.save(new User("Other", "other@example.com", "pass", Role.ROLE_USER));

        IdeaRequest req = new IdeaRequest();
        req.setTitle("Autonomous Drone Delivery");
        req.setDescription("Route planning algorithm for UAV fleet delivery");
        req.setMode(IdeaMode.HACKATHON);
        idea = ideaService.createIdea(req, ideaOwner);
    }

    @Test
    void testCreateAndUpdateExperimentLifecycle() {
        ExperimentRequest req = new ExperimentRequest();
        req.setTitle("GPS Waypoint Latency Test");
        req.setHypothesis("Direct MQTT websocket reduces latency below 50ms");
        req.setObjective("Benchmark round-trip telemetry transmission");
        req.setSteps("1. Send 1000 telemetry packets; 2. Measure ack timestamp");
        req.setSuccessCriteria("Latency p99 < 50ms");
        req.setStatus(ExperimentStatus.IN_PROGRESS);

        ExperimentResponse created = experimentService.createExperiment(idea.getId(), req, ideaOwner);
        assertNotNull(created.getId());
        assertEquals("GPS Waypoint Latency Test", created.getTitle());
        assertEquals(ExperimentStatus.IN_PROGRESS, created.getStatus());

        // Update experiment with observations and complete it
        ExperimentRequest updateReq = new ExperimentRequest();
        updateReq.setTitle(created.getTitle());
        updateReq.setHypothesis(created.getHypothesis());
        updateReq.setObjective(created.getObjective());
        updateReq.setObservations("Observed average latency of 38ms across 1000 pings");
        updateReq.setResults("Success: p99 latency was 46ms");
        updateReq.setStatus(ExperimentStatus.COMPLETED);

        ExperimentResponse updated = experimentService.updateExperiment(created.getId(), updateReq, ideaOwner);
        assertEquals(ExperimentStatus.COMPLETED, updated.getStatus());
        assertEquals("Success: p99 latency was 46ms", updated.getResults());

        List<ExperimentResponse> list = experimentService.getExperimentsForIdea(idea.getId());
        assertEquals(1, list.size());
    }

    @Test
    void testOtherUserCannotCreateExperimentOnSomeoneElsesIdea() {
        ExperimentRequest req = new ExperimentRequest();
        req.setTitle("Unauthorized Test");
        req.setHypothesis("Hypothesis");
        req.setObjective("Objective");

        assertThrows(UnauthorizedException.class, () ->
                experimentService.createExperiment(idea.getId(), req, otherUser));
    }
}
