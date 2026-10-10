package com.ideaforge.repository;

import com.ideaforge.entity.Experiment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentRepository extends JpaRepository<Experiment, Long> {
    List<Experiment> findByIdeaIdOrderByCreatedAtDesc(Long ideaId);
    List<Experiment> findByUserIdOrderByCreatedAtDesc(Long userId);
}
