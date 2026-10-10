package com.ideaforge;

import com.ideaforge.dto.SimilarityMatchResponse;
import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import com.ideaforge.entity.Role;
import com.ideaforge.entity.User;
import com.ideaforge.repository.IdeaRepository;
import com.ideaforge.repository.UserRepository;
import com.ideaforge.service.SimilarityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SimilarityServiceTest {

    @Autowired
    private SimilarityService similarityService;

    @Autowired
    private IdeaRepository ideaRepository;

    @Autowired
    private UserRepository userRepository;

    private Idea idea1;
    private Idea idea2;
    private Idea idea3;

    @BeforeEach
    void setUp() {
        ideaRepository.deleteAll();
        userRepository.deleteAll();

        User user = userRepository.save(new User("Dev Tester", "dev@example.com", "pass123", Role.ROLE_USER));

        idea1 = new Idea();
        idea1.setTitle("Autonomous Drone Delivery");
        idea1.setDescription("Campus drone delivery system for library books and urgent medical kits");
        idea1.setTags("drone, delivery, campus, robotics");
        idea1.setMode(IdeaMode.HACKATHON);
        idea1.setUser(user);
        idea1 = ideaRepository.save(idea1);

        idea2 = new Idea();
        idea2.setTitle("Automated Drone Logistics");
        idea2.setDescription("Last-mile drone delivery network for college campuses and smart towns");
        idea2.setTags("drone, delivery, logistics, iot");
        idea2.setMode(IdeaMode.HACKATHON);
        idea2.setUser(user);
        idea2 = ideaRepository.save(idea2);

        idea3 = new Idea();
        idea3.setTitle("Online Vegan Bakery");
        idea3.setDescription("E-commerce shop for organic vegan cakes and gluten free pastries");
        idea3.setTags("bakery, food, dessert, e-commerce");
        idea3.setMode(IdeaMode.STARTUP);
        idea3.setUser(user);
        idea3 = ideaRepository.save(idea3);
    }

    @Test
    void testFindSimilarIdeasMatchesRelatedAndExcludesUnrelated() {
        List<SimilarityMatchResponse> matches = similarityService.findSimilarIdeas(idea1.getId(), 10);

        assertFalse(matches.isEmpty(), "Should find matches for drone delivery");
        assertEquals(idea2.getId(), matches.get(0).getId());
        assertTrue(matches.get(0).getMatchPercentage() > 25);
        assertTrue(matches.get(0).getCommonKeywords().contains("drone") || matches.get(0).getCommonKeywords().contains("delivery"));

        // Unrelated idea should not be the top match
        boolean containsBakery = matches.stream().anyMatch(m -> m.getId().equals(idea3.getId()));
        assertFalse(containsBakery, "Unrelated idea should be filtered out by threshold");
    }
}
