package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.SightingRepository;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SightingServiceTest {

    @Mock
    private SightingRepository sightingRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SightingService sightingService;

    private User testUser;
    private Sighting testSighting;
    private Sighting savedSighting;

    // Helper method to set ID using reflection
    private void setId(Object obj, Long id) {
        try {
            Field idField = obj.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(obj, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @BeforeEach
    void setUp() {
        testUser = new User("testuser", "password123");
        setId(testUser, 1L);
        testUser.setTotalAnimalsLogged(0);
        testUser.setUniqueSpeciesCount(0);

        testSighting = new Sighting("Gray Wolf", "Yellowstone", "Howling at moon", "url1", testUser);
        savedSighting = new Sighting("Gray Wolf", "Yellowstone", "Howling at moon", "url1", testUser);
        setId(savedSighting, 1L);
    }

    // ==================== CREATE TESTS ====================

    @Test
    void testCreateSighting_Success() {
        // Arrange
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of(savedSighting));

        // Act
        Sighting result = sightingService.createSighting(testSighting);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Gray Wolf", result.getSpecies());
        assertEquals("Yellowstone", result.getLocation());
        verify(sightingRepository).save(testSighting);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void testCreateSighting_WithNullUser() {
        // Arrange
        Sighting sightingWithoutUser = new Sighting("Gray Wolf", "Yellowstone", "Description", "url", null);
        when(sightingRepository.save(any(Sighting.class))).thenReturn(sightingWithoutUser);

        // Act
        Sighting result = sightingService.createSighting(sightingWithoutUser);

        // Assert
        assertNotNull(result);
        assertNull(result.getUser());
        verify(sightingRepository).save(sightingWithoutUser);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testCreateSighting_UpdatesUserStatistics() {
        // Arrange
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of(savedSighting));

        // Act
        sightingService.createSighting(testSighting);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals(1, testUser.getTotalAnimalsLogged());
        assertEquals(1, testUser.getUniqueSpeciesCount());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    void testFindById_Success() {
        // Arrange
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(savedSighting));

        // Act
        Sighting result = sightingService.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Gray Wolf", result.getSpecies());
        verify(sightingRepository).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(sightingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sightingService.findById(999L);
        });

        assertEquals("Sighting not found", exception.getMessage());
        verify(sightingRepository).findById(999L);
    }

    // ==================== FIND ALL TESTS ====================

    @Test
    void testFindAll_Success() {
        // Arrange
        Sighting sighting2 = new Sighting("Bald Eagle", "Alaska", "Flying", "url2", testUser);
        setId(sighting2, 2L);
        List<Sighting> sightings = Arrays.asList(savedSighting, sighting2);
        when(sightingRepository.findAll()).thenReturn(sightings);

        // Act
        List<Sighting> result = sightingService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(sightingRepository).findAll();
    }

    @Test
    void testFindAll_EmptyList() {
        // Arrange
        when(sightingRepository.findAll()).thenReturn(List.of());

        // Act
        List<Sighting> result = sightingService.findAll();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(sightingRepository).findAll();
    }

    // ==================== UPDATE TESTS ====================

    @Test
    void testUpdate_Success() {
        // Arrange
        Sighting updatedSighting = new Sighting("Red Wolf", "North Carolina", "Updated description", "url2", testUser);
        
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(savedSighting));
        when(sightingRepository.save(any(Sighting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of(savedSighting));

        // Act
        Sighting result = sightingService.update(1L, updatedSighting);

        // Assert
        assertNotNull(result);
        assertEquals("Red Wolf", result.getSpecies());
        assertEquals("North Carolina", result.getLocation());
        assertEquals("Updated description", result.getDescription());
        verify(sightingRepository).findById(1L);
        verify(sightingRepository).save(savedSighting);
    }

    @Test
    void testUpdate_NotFound() {
        // Arrange
        Sighting updatedSighting = new Sighting("Red Wolf", "North Carolina", "Description", "url", testUser);
        when(sightingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sightingService.update(999L, updatedSighting);
        });

        assertEquals("Sighting not found", exception.getMessage());
        verify(sightingRepository).findById(999L);
        verify(sightingRepository, never()).save(any(Sighting.class));
    }

    @Test
    void testUpdate_UpdatesUserStatistics() {
        // Arrange
        Sighting updatedSighting = new Sighting("Red Wolf", "North Carolina", "Description", "url", testUser);
        
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(savedSighting));
        when(sightingRepository.save(any(Sighting.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of(savedSighting));

        // Act
        sightingService.update(1L, updatedSighting);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    // ==================== DELETE TESTS ====================

    @Test
    void testDelete_Success() {
        // Arrange
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(savedSighting));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of());

        // Act
        sightingService.delete(1L);

        // Assert
        verify(sightingRepository).findById(1L);
        verify(sightingRepository).deleteById(1L);
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
    }

    @Test
    void testDelete_NotFound() {
        // Arrange
        when(sightingRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            sightingService.delete(999L);
        });

        assertEquals("Sighting not found", exception.getMessage());
        verify(sightingRepository).findById(999L);
        verify(sightingRepository, never()).deleteById(anyLong());
    }

    @Test
    void testDelete_WithNullUser() {
        // Arrange
        Sighting sightingWithoutUser = new Sighting("Gray Wolf", "Yellowstone", "Description", "url", null);
        setId(sightingWithoutUser, 1L);
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(sightingWithoutUser));

        // Act
        sightingService.delete(1L);

        // Assert
        verify(sightingRepository).findById(1L);
        verify(sightingRepository).deleteById(1L);
        verify(userRepository, never()).findById(anyLong());
    }

    @Test
    void testDelete_UpdatesUserStatistics() {
        // Arrange
        when(sightingRepository.findById(1L)).thenReturn(Optional.of(savedSighting));
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of()); // No sightings after deletion

        // Act
        sightingService.delete(1L);

        // Assert
        verify(userRepository).findById(1L);
        verify(userRepository).save(testUser);
        assertEquals(0, testUser.getTotalAnimalsLogged());
        assertEquals(0, testUser.getUniqueSpeciesCount());
    }

    // ==================== STATISTICS UPDATE TESTS ====================

    @Test
    void testUpdateUserStatistics_MultipleSightingsSameSpecies() {
        // Arrange
        Sighting sighting1 = new Sighting("Gray Wolf", "Yellowstone", "Description", "url1", testUser);
        Sighting sighting2 = new Sighting("Gray Wolf", "Yellowstone", "Description", "url2", testUser);
        List<Sighting> userSightings = Arrays.asList(sighting1, sighting2);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(userSightings);
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);

        // Act
        sightingService.createSighting(testSighting);

        // Assert
        verify(userRepository).save(testUser);
        // After creating one more, should have 3 total but 1 unique species
    }

    @Test
    void testUpdateUserStatistics_MultipleUniqueSpecies() {
        // Arrange
        Sighting sighting1 = new Sighting("Gray Wolf", "Yellowstone", "Description", "url1", testUser);
        Sighting sighting2 = new Sighting("Bald Eagle", "Alaska", "Description", "url2", testUser);
        Sighting sighting3 = new Sighting("Grizzly Bear", "Montana", "Description", "url3", testUser);
        List<Sighting> userSightings = Arrays.asList(sighting1, sighting2, sighting3);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(userSightings);
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);

        // Act
        sightingService.createSighting(testSighting);

        // Assert
        verify(userRepository).save(testUser);
        // Should update unique species count to 3
    }

    @Test
    void testUpdateUserStatistics_UpdatesLastActivityDate() {
        // Arrange
        LocalDateTime beforeUpdate = LocalDateTime.now().minusHours(1);
        testUser.setLastActivityDate(beforeUpdate);
        
        when(sightingRepository.save(any(Sighting.class))).thenReturn(savedSighting);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(sightingRepository.findByUserId(1L)).thenReturn(List.of(savedSighting));

        // Act
        sightingService.createSighting(testSighting);

        // Assert
        verify(userRepository).save(testUser);
        assertNotNull(testUser.getLastActivityDate());
        assertTrue(testUser.getLastActivityDate().isAfter(beforeUpdate));
    }
}

