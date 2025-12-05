package com.team4.wildlifetracker.service;

import com.team4.wildlifetracker.model.Profile;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.ProfileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing user profiles and statistics.
 * Handles profile creation, updates, and retrieval operations.
 */
@Service
@Transactional
public class ProfileService {

    private static final Logger logger = LoggerFactory.getLogger(ProfileService.class);
    
    private final ProfileRepository profileRepository;

    public ProfileService(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    /**
     * Create a new profile for a user.
     * @param user The user to create a profile for
     * @return The created profile
     */
    public Profile createProfile(User user) {
        logger.info("Creating profile for user: {}", user.getId());
        Profile profile = new Profile(user);
        return profileRepository.save(profile);
    }

    /**
     * Get profile by user ID.
     * @param userId The user ID
     * @return Optional containing the profile if found
     */
    @Transactional(readOnly = true)
    public Optional<Profile> getProfileByUserId(Long userId) {
        return profileRepository.findByUserId(userId);
    }

    /**
     * Get profile by user entity.
     * @param user The user
     * @return Optional containing the profile if found
     */
    @Transactional(readOnly = true)
    public Optional<Profile> getProfileByUser(User user) {
        return profileRepository.findByUser(user);
    }

    /**
     * Get or create profile for a user.
     * @param user The user
     * @return The profile (existing or newly created)
     */
    public Profile getOrCreateProfile(User user) {
        return profileRepository.findByUser(user)
                .orElseGet(() -> createProfile(user));
    }

    /**
     * Update profile statistics.
     * @param profile The profile to update
     * @return The updated profile
     */
    public Profile updateProfile(Profile profile) {
        logger.info("Updating profile: {}", profile.getId());
        return profileRepository.save(profile);
    }

    /**
     * Increment animals logged counter.
     * @param userId The user ID
     */
    public void incrementAnimalsLogged(Long userId) {
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.incrementAnimalsLogged();
            profileRepository.save(profile);
            logger.info("Incremented animals logged for user: {}", userId);
        });
    }

    /**
     * Increment assists counter.
     * @param userId The user ID
     */
    public void incrementAssists(Long userId) {
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.incrementAssists();
            profileRepository.save(profile);
            logger.info("Incremented assists for user: {}", userId);
        });
    }

    /**
     * Update most frequent area.
     * @param userId The user ID
     * @param area The area name
     */
    public void updateMostFrequentArea(Long userId, String area) {
        profileRepository.findByUserId(userId).ifPresent(profile -> {
            profile.setMostFrequentArea(area);
            profileRepository.save(profile);
            logger.info("Updated most frequent area for user {}: {}", userId, area);
        });
    }

    /**
     * Get top profiles by animals logged.
     * @return List of top profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> getTopProfilesByAnimalsLogged() {
        return profileRepository.findTopByAnimalsLogged();
    }

    /**
     * Get top profiles by species seen.
     * @return List of top profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> getTopProfilesBySpeciesSeen() {
        return profileRepository.findTopBySpeciesSeen();
    }

    /**
     * Get top profiles by assists.
     * @return List of top profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> getTopProfilesByAssists() {
        return profileRepository.findTopByAssists();
    }

    /**
     * Delete a profile.
     * @param profileId The profile ID
     */
    public void deleteProfile(Long profileId) {
        logger.info("Deleting profile: {}", profileId);
        profileRepository.deleteById(profileId);
    }

    /**
     * Get all profiles.
     * @return List of all profiles
     */
    @Transactional(readOnly = true)
    public List<Profile> getAllProfiles() {
        return profileRepository.findAll();
    }
}
