package com.aidsamentor.problem.repository;

import com.aidsamentor.problem.entity.Difficulty;
import com.aidsamentor.problem.entity.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long> {

    boolean existsByTitleIgnoreCase(String title);

    boolean existsByTitleIgnoreCaseAndIdNot(String title, Long id);

    List<Problem> findByDifficulty(Difficulty difficulty);

    @Query("select distinct p from Problem p join p.tags tag where lower(tag) = lower(:tag)")
    List<Problem> findByTagIgnoreCase(@Param("tag") String tag);
}
