package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.LeaderboardEntry;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaderboardServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private LeaderboardService leaderboardService;

    private User user1;
    private User user2;
    private User user3;

    // Helper method to set ID using reflection
    private void setId(User user, Long id) {
        try {
            Field idField = User.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(user, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @BeforeEach
    void setUp() {
        user1 = new User("user1", "password");
        setId(user1, 1L);
        user1.setDisplayName("User One");
        user1.setTotalAnimalsLogged(10);
        user1.setUniqueSpeciesCount(5);
        user1.setLastActivityDate(LocalDateTime.now().minusDays(1));

        user2 = new User("user2", "password");
        setId(user2, 2L);
        user2.setDisplayName("User Two");
        user2.setTotalAnimalsLogged(15);
        user2.setUniqueSpeciesCount(8);
        user2.setLastActivityDate(LocalDateTime.now().minusHours(5));

        user3 = new User("user3", "password");
        setId(user3, 3L);
        user3.setDisplayName(null); // No display name
        user3.setTotalAnimalsLogged(5);
        user3.setUniqueSpeciesCount(3);
        user3.setLastActivityDate(LocalDateTime.now().minusDays(2));
    }

    // ==================== GET LEADERBOARD TESTS ====================

    @Test
    void testGetLeaderboard_Success() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3); // Ordered by rank
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        
        // Check first place
        assertEquals(1, result.get(0).getRank());
        assertEquals(2L, result.get(0).getUserId());
        assertEquals("user2", result.get(0).getUsername());
        assertEquals("User Two", result.get(0).getDisplayName());
        assertEquals(15, result.get(0).getTotalAnimalsLogged());
        assertEquals(8, result.get(0).getUniqueSpeciesCount());
        
        // Check second place
        assertEquals(2, result.get(1).getRank());
        assertEquals(1L, result.get(1).getUserId());
        
        // Check third place
        assertEquals(3, result.get(2).getRank());
        assertEquals(3L, result.get(2).getUserId());
        
        verify(userRepository).findAllOrderedByLeaderboardRank();
    }

    @Test
    void testGetLeaderboard_EmptyList() {
        // Arrange
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of());

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(userRepository).findAllOrderedByLeaderboardRank();
    }

    @Test
    void testGetLeaderboard_SingleUser() {
        // Arrange
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of(user1));

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(1L, result.get(0).getUserId());
        assertEquals("user1", result.get(0).getUsername());
    }

    @Test
    void testGetLeaderboard_UserWithoutDisplayName() {
        // Arrange
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of(user3));

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertEquals(1, result.size());
        assertEquals("user3", result.get(0).getDisplayName()); // Should use username when displayName is null
    }

    @Test
    void testGetLeaderboard_UserWithNullProfilePicture() {
        // Arrange
        user1.setProfilePictureUrl(null);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of(user1));

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertEquals(1, result.size());
        assertNull(result.get(0).getProfilePictureUrl());
    }

    // ==================== GET TOP N TESTS ====================

    @Test
    void testGetTopN_Success() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getTopN(2);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(1, result.get(0).getRank());
        assertEquals(2, result.get(1).getRank());
    }

    @Test
    void testGetTopN_RequestMoreThanAvailable() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getTopN(10);

        // Assert
        assertEquals(2, result.size()); // Should return only available users
    }

    @Test
    void testGetTopN_RequestZero() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getTopN(0);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetTopN_RequestOne() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getTopN(1);

        // Assert
        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getRank());
    }

    @Test
    void testGetTopN_EmptyLeaderboard() {
        // Arrange
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of());

        // Act
        List<LeaderboardEntry> result = leaderboardService.getTopN(5);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    // ==================== GET USER RANK TESTS ====================

    @Test
    void testGetUserRank_Success() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        LeaderboardEntry result = leaderboardService.getUserRank(1L);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.getRank()); // user1 is second place
        assertEquals(1L, result.getUserId());
        assertEquals("user1", result.getUsername());
    }

    @Test
    void testGetUserRank_FirstPlace() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        LeaderboardEntry result = leaderboardService.getUserRank(2L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getRank()); // user2 is first place
        assertEquals(2L, result.getUserId());
    }

    @Test
    void testGetUserRank_LastPlace() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        LeaderboardEntry result = leaderboardService.getUserRank(3L);

        // Assert
        assertNotNull(result);
        assertEquals(3, result.getRank()); // user3 is third place
        assertEquals(3L, result.getUserId());
    }

    @Test
    void testGetUserRank_UserNotFound() {
        // Arrange
        List<User> users = Arrays.asList(user2, user1, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(users);

        // Act
        LeaderboardEntry result = leaderboardService.getUserRank(999L);

        // Assert
        assertNull(result);
    }

    @Test
    void testGetUserRank_EmptyLeaderboard() {
        // Arrange
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of());

        // Act
        LeaderboardEntry result = leaderboardService.getUserRank(1L);

        // Assert
        assertNull(result);
    }

    // ==================== EDGE CASES ====================

    @Test
    void testGetLeaderboard_UsersWithZeroStatistics() {
        // Arrange
        User userWithZeros = new User("zerouser", "password");
        setId(userWithZeros, 4L);
        userWithZeros.setTotalAnimalsLogged(0);
        userWithZeros.setUniqueSpeciesCount(0);
        userWithZeros.setLastActivityDate(null);
        
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(List.of(userWithZeros));

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertEquals(1, result.size());
        assertEquals(0, result.get(0).getTotalAnimalsLogged());
        assertEquals(0, result.get(0).getUniqueSpeciesCount());
        assertNull(result.get(0).getLastActivityDate());
    }

    @Test
    void testGetLeaderboard_LargeNumberOfUsers() {
        // Arrange
        List<User> manyUsers = Arrays.asList(user2, user1, user3, user1, user2, user3);
        when(userRepository.findAllOrderedByLeaderboardRank()).thenReturn(manyUsers);

        // Act
        List<LeaderboardEntry> result = leaderboardService.getLeaderboard();

        // Assert
        assertEquals(6, result.size());
        // Verify ranks are sequential
        for (int i = 0; i < result.size(); i++) {
            assertEquals(i + 1, result.get(i).getRank());
        }
    }
}

