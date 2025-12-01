package com.team4.wildlifetracker;

import com.team4.wildlifetracker.model.Notification;
import com.team4.wildlifetracker.model.Sighting;
import com.team4.wildlifetracker.model.User;
import com.team4.wildlifetracker.repository.NotificationRepository;
import com.team4.wildlifetracker.repository.SightingRepository;
import com.team4.wildlifetracker.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class SearchAndNotificationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SightingRepository sightingRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private User testUser;

    @BeforeEach
    public void setup() {
        notificationRepository.deleteAll();
        sightingRepository.deleteAll();
        userRepository.deleteAll();

        testUser = new User("testuser", "password");
        testUser = userRepository.save(testUser);
    }

    @Test
    public void testSearchSightings() throws Exception {
        Sighting s1 = new Sighting("Gray Wolf", "Yellowstone", "Howling", "url", testUser);
        Sighting s2 = new Sighting("Bald Eagle", "Alaska", "Flying", "url", testUser);
        sightingRepository.save(s1);
        sightingRepository.save(s2);

        // Search by species
        mockMvc.perform(get("/api/search").param("query", "Wolf"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].species", is("Gray Wolf")));

        // Search by location
        mockMvc.perform(get("/api/search").param("query", "Alaska"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].species", is("Bald Eagle")));
    }

    @Test
    public void testNotifications() throws Exception {
        Notification n1 = new Notification("Welcome!", testUser);
        notificationRepository.save(n1);

        // Get notifications
        mockMvc.perform(get("/api/notifications").param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].message", is("Welcome!")))
                .andExpect(jsonPath("$[0].read", is(false)));

        // Mark as read
        mockMvc.perform(post("/api/notifications/" + n1.getId() + "/read"))
                .andExpect(status().isOk());

        // Verify read status
        mockMvc.perform(get("/api/notifications").param("userId", testUser.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].read", is(true)));
    }
}
