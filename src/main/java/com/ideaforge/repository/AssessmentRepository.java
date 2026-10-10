package com.ideaforge.repository;

import com.ideaforge.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {
    List<Assessment> findByIdeaIdOrderByCreatedAtDesc(Long ideaId);
    Optional<Assessment> findFirstByIdeaIdOrderByCreatedAtDesc(Long ideaId);
}
