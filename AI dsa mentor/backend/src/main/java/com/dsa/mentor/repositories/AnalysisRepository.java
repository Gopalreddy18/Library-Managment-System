package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.Analysis;
import com.dsa.mentor.entities.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AnalysisRepository extends JpaRepository<Analysis, Long> {
    Optional<Analysis> findBySubmission(Submission submission);
}
