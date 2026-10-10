package com.ideaforge;

import com.ideaforge.dto.IdeaRequest;
import com.ideaforge.dto.IdeaResponse;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.exception.UnauthorizedException;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.IdeaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class IdeaServiceTest {

    @Autowired
    private IdeaService ideaService;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    private User owner;
    private User otherUser;
    private User admin;

    @BeforeEach
    void setUp() {
        ideaRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(new User("Owner", "owner@example.com", "pass", Role.ROLE_USER));
        otherUser = userRepository.save(new User("Other", "other@example.com", "pass", Role.ROLE_USER));
        admin = userRepository.save(new User("Admin", "admin@example.com", "pass", Role.ROLE_ADMIN));
    }

    @Test
    void testCreateAndRetrieveIdea() {
        IdeaRequest req = new IdeaRequest();
        req.setTitle("EcoTracker IoT");
        req.setDescription("Real-time carbon footprint tracker using microcontrollers");
        req.setMode(IdeaMode.COLLEGE_PROJECT);
        req.setTechStack("ESP32, Spring Boot, MySQL");

        IdeaResponse created = ideaService.createIdea(req, owner);
        assertNotNull(created.getId());
        assertEquals("EcoTracker IoT", created.getTitle());
        assertTrue(created.getOverallScore() > 0, "Idea should be immediately scored upon creation");

        IdeaResponse retrieved = ideaService.getIdeaById(created.getId());
        assertEquals("EcoTracker IoT", retrieved.getTitle());
    }

    @Test
    void testOtherUserCannotEditOrDeleteIdea() {
        IdeaRequest req = new IdeaRequest();
        req.setTitle("Secret Project");
        req.setDescription("Proprietary system");
        req.setMode(IdeaMode.STARTUP);
        IdeaResponse created = ideaService.createIdea(req, owner);

        IdeaRequest updateReq = new IdeaRequest();
        updateReq.setTitle("Hacked Title");
        updateReq.setDescription("Hacked Description");
        updateReq.setMode(IdeaMode.STARTUP);

        assertThrows(UnauthorizedException.class, () -> ideaService.updateIdea(created.getId(), updateReq, otherUser));
        assertThrows(UnauthorizedException.class, () -> ideaService.deleteIdea(created.getId(), otherUser));
    }

    @Test
    void testAdminCanModifyAnyIdea() {
        IdeaRequest req = new IdeaRequest();
        req.setTitle("Community Hub");
        req.setDescription("Open community center software");
        req.setMode(IdeaMode.OPEN_INNOVATION);
        IdeaResponse created = ideaService.createIdea(req, owner);

        assertDoesNotThrow(() -> ideaService.deleteIdea(created.getId(), admin));
    }
}
