package com.dsa.mentor.services;

import com.dsa.mentor.entities.User;
import com.dsa.mentor.entities.UserRole;
import com.dsa.mentor.entities.UserStatistics;
import com.dsa.mentor.repositories.UserRepository;
import com.dsa.mentor.repositories.UserStatisticsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStatisticsRepository userStatisticsRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetUserByIdSuccess() {
        User user = User.builder()
            .id(1L)
            .name("Jane Doe")
            .email("jane@test.com")
            .role(UserRole.ROLE_STUDENT)
            .build();
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        
        User result = userService.getUserById(1L);
        assertNotNull(result);
        assertEquals("Jane Doe", result.getName());
        assertEquals("jane@test.com", result.getEmail());
    }

    @Test
    public void testGetOrCreateUserStatisticsExists() {
        User user = User.builder().id(1L).build();
        UserStatistics stats = UserStatistics.builder().user(user).solvedCount(5).build();

        when(userStatisticsRepository.findByUser(user)).thenReturn(Optional.of(stats));

        UserStatistics result = userService.getOrCreateUserStatistics(user);
        assertEquals(5, result.getSolvedCount());
        verify(userStatisticsRepository, never()).save(any());
    }

    @Test
    public void testGetOrCreateUserStatisticsNew() {
        User user = User.builder().id(1L).build();
        UserStatistics stats = UserStatistics.builder().user(user).solvedCount(0).build();

        when(userStatisticsRepository.findByUser(user)).thenReturn(Optional.empty());
        when(userStatisticsRepository.save(any(UserStatistics.class))).thenReturn(stats);

        UserStatistics result = userService.getOrCreateUserStatistics(user);
        assertNotNull(result);
        assertEquals(0, result.getSolvedCount());
        verify(userStatisticsRepository, times(1)).save(any(UserStatistics.class));
    }
}
