package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.dto.ProfileUpdateRequest;
import com.team4.wildlifetracker.dto.UserResponse;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private User savedUser;

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
        testUser = new User("testuser", "password123");
        savedUser = new User("testuser", "password123");
        setId(savedUser, 1L);
    }

    // ==================== REGISTRATION TESTS ====================

    @Test
    void testRegisterUser_Success() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse result = userService.registerUser("testuser", "password123");

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertNull(result.getDisplayName()); // DTO doesn't expose password
        verify(userRepository).findByUsername("testuser");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void testRegisterUser_DuplicateUsername() {
        // Arrange
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(savedUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.registerUser("existinguser", "password123");
        });

        assertEquals("Username already exists", exception.getMessage());
        verify(userRepository).findByUsername("existinguser");
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testRegisterUser_EmptyUsername() {
        // Arrange
        when(userRepository.findByUsername("")).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        // Act
        UserResponse result = userService.registerUser("", "password123");

        // Assert
        assertNotNull(result);
        verify(userRepository).findByUsername("");
    }

    // ==================== LOGIN TESTS ====================

    @Test
    void testLogin_ValidCredentials() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

        // Act
        Optional<UserResponse> result = userService.login("testuser", "password123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
        assertNotNull(result.get().getId());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLogin_InvalidUsername() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<UserResponse> result = userService.login("nonexistent", "password123");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("nonexistent");
    }

    @Test
    void testLogin_InvalidPassword() {
        // Arrange
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(savedUser));

        // Act
        Optional<UserResponse> result = userService.login("testuser", "wrongpassword");

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findByUsername("testuser");
    }

    @Test
    void testLogin_EmptyPassword() {
        // Arrange
        User userWithEmptyPassword = new User("testuser", "");
        setId(userWithEmptyPassword, 1L);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userWithEmptyPassword));

        // Act
        Optional<UserResponse> result = userService.login("testuser", "");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("testuser", result.get().getUsername());
    }

    // ==================== FIND BY ID TESTS ====================

    @Test
    void testFindById_UserExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        // Act
        Optional<User> result = userService.findById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("testuser", result.get().getUsername());
        verify(userRepository).findById(1L);
    }

    @Test
    void testFindById_UserNotExists() {
        // Arrange
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<User> result = userService.findById(999L);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository).findById(999L);
    }

    // ==================== PROFILE UPDATE TESTS ====================

    @Test
    void testUpdateProfile_AllFields() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setDisplayName("John Doe");
        request.setBio("Wildlife enthusiast");
        request.setProfilePictureUrl("/uploads/pic.jpg");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateProfile(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("John Doe", result.getDisplayName());
        assertEquals("Wildlife enthusiast", result.getBio());
        assertEquals("/uploads/pic.jpg", result.getProfilePictureUrl());
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUpdateProfile_OnlyDisplayName() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setDisplayName("Jane Doe");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateProfile(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Jane Doe", result.getDisplayName());
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUpdateProfile_OnlyBio() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setBio("Nature lover");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateProfile(1L, request);

        // Assert
        assertNotNull(result);
        assertEquals("Nature lover", result.getBio());
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUpdateProfile_EmptyDisplayName() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setDisplayName("   "); // Whitespace only

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateProfile(1L, request);

        // Assert
        assertNotNull(result);
        // Display name should not be updated if empty/whitespace
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUpdateProfile_NullFields() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        // All fields are null

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        UserResponse result = userService.updateProfile(1L, request);

        // Assert
        assertNotNull(result);
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUpdateProfile_UserNotFound() {
        // Arrange
        ProfileUpdateRequest request = new ProfileUpdateRequest();
        request.setDisplayName("John Doe");

        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.updateProfile(999L, request);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    // ==================== PROFILE PICTURE UPLOAD TESTS ====================

    @Test
    void testUploadProfilePicture_Success() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("photo.jpg");
        when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[100]));

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = userService.uploadProfilePicture(1L, file);

        // Assert
        assertNotNull(result);
        assertTrue(result.startsWith("/uploads/profile-pictures/"));
        assertTrue(result.contains("profile_1_"));
        verify(userRepository).findById(1L);
        verify(userRepository).save(savedUser);
    }

    @Test
    void testUploadProfilePicture_EmptyFile() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.uploadProfilePicture(1L, file);
        });

        assertEquals("File is empty", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUploadProfilePicture_InvalidFileType() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("text/plain");

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.uploadProfilePicture(1L, file);
        });

        assertEquals("File must be an image", exception.getMessage());
        verify(userRepository).findById(1L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUploadProfilePicture_NullContentType() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn(null);

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.uploadProfilePicture(1L, file);
        });

        assertEquals("File must be an image", exception.getMessage());
    }

    @Test
    void testUploadProfilePicture_UserNotFound() {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            userService.uploadProfilePicture(999L, file);
        });

        assertEquals("User not found", exception.getMessage());
        verify(userRepository).findById(999L);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void testUploadProfilePicture_NoFileExtension() throws IOException {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getContentType()).thenReturn("image/jpeg");
        when(file.getOriginalFilename()).thenReturn("photo"); // No extension
        when(file.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[100]));

        when(userRepository.findById(1L)).thenReturn(Optional.of(savedUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        String result = userService.uploadProfilePicture(1L, file);

        // Assert
        assertNotNull(result);
        assertTrue(result.endsWith(".jpg")); // Should default to .jpg
    }
}

