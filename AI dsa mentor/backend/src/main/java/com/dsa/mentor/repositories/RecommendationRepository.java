package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.Recommendation;
import com.dsa.mentor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUserOrderByCreatedAtDesc(User user);
    void deleteByUser(User user);
}
