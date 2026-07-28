package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.Problem;
import com.dsa.mentor.entities.Submission;
import com.dsa.mentor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByUserOrderBySubmissionTimeDesc(User user);
    List<Submission> findByUserAndProblemOrderBySubmissionTimeDesc(User user, Problem problem);
    long countByUser(User user);
    
    @Query("SELECT COUNT(DISTINCT s.problem.id) FROM Submission s WHERE s.user = :user AND s.result = 'ACCEPTED'")
    long countSolvedByUser(User user);
}
