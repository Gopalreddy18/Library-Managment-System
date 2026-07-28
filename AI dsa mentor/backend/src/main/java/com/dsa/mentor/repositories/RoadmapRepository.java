package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.Roadmap;
import com.dsa.mentor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoadmapRepository extends JpaRepository<Roadmap, Long> {
    List<Roadmap> findByUserOrderByCreatedAtDesc(User user);
    Optional<Roadmap> findFirstByUserOrderByCreatedAtDesc(User user);
}
