package com.ideaforge.repository;

import com.ideaforge.entity.Idea;
import com.ideaforge.entity.IdeaMode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Long> {

    List<Idea> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Idea> findAllByOrderByCreatedAtDesc();

    List<Idea> findByModeOrderByCreatedAtDesc(IdeaMode mode);

    @Query("SELECT i FROM Idea i WHERE " +
           "LOWER(i.title) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.tagline) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(i.tags) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Idea> searchIdeas(@Param("query") String query);

    @Query("SELECT i FROM Idea i WHERE i.id <> :excludeId")
    List<Idea> findAllExceptId(@Param("excludeId") Long excludeId);
}
