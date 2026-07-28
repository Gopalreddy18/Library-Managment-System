package com.dsa.mentor.repositories;

import com.dsa.mentor.entities.MockInterview;
import com.dsa.mentor.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockInterviewRepository extends JpaRepository<MockInterview, Long> {
    List<MockInterview> findByUserOrderByCreatedAtDesc(User user);
}
