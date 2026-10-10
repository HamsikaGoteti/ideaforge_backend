package com.ideaforge.repository;

import com.ideaforge.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByIdeaIdOrderByCreatedAtDesc(Long ideaId);
    List<Feedback> findByUserIdOrderByCreatedAtDesc(Long userId);
}
