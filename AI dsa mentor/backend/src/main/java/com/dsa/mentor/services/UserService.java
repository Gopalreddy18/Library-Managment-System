package com.dsa.mentor.services;

import com.dsa.mentor.entities.User;
import com.dsa.mentor.entities.UserStatistics;
import com.dsa.mentor.exceptions.ResourceNotFoundException;
import com.dsa.mentor.repositories.UserRepository;
import com.dsa.mentor.repositories.UserStatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserStatisticsRepository userStatisticsRepository;

    public User getUserById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
    }

    public User updateProfile(Long userId, User profileDetails) {
        User user = getUserById(userId);
        user.setName(profileDetails.getName());
        user.setCollege(profileDetails.getCollege());
        user.setGraduationYear(profileDetails.getGraduationYear());
        user.setTargetCompany(profileDetails.getTargetCompany());
        user.setTargetRole(profileDetails.getTargetRole());
        user.setCurrentDsaLevel(profileDetails.getCurrentDsaLevel());
        if (profileDetails.getProfilePhoto() != null) {
            user.setProfilePhoto(profileDetails.getProfilePhoto());
        }
        return userRepository.save(user);
    }

    public UserStatistics getOrCreateUserStatistics(User user) {
        Optional<UserStatistics> statsOpt = userStatisticsRepository.findByUser(user);
        if (statsOpt.isPresent()) {
            return statsOpt.get();
        } else {
            UserStatistics stats = UserStatistics.builder()
                .user(user)
                .solvedCount(0)
                .successRate(0.0)
                .averageDifficulty(0.0)
                .weakTopics("")
                .strongTopics("")
                .dailyActivityData("{}")
                .build();
            return userStatisticsRepository.save(stats);
        }
    }
}
