package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.Difficulty;
import com.dsa.mentor.entities.Problem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProblemRepository extends JpaRepository<Problem, Long> {
    List<Problem> findByDifficulty(Difficulty difficulty);
    
    @Query("SELECT p FROM Problem p WHERE p.tags LIKE %:tag%")
    List<Problem> findByTag(@Param("tag") String tag);
}
