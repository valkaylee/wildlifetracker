package com.team4.wildlifetracker.auth;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(unique = true)
    private String email;
    @Column(name="password_hash")
    private String passwordHash;
    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;
    @Column(name="notifications_email")
    private boolean notificationsEmail = true;
    @Column(name="notifications_sms")
    private boolean notificationsSms = false;
    @Column(name="created_at")
    private Instant createdAt = Instant.now();

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public Role getRole() { return role; }
    public boolean isNotificationsEmail() { return notificationsEmail; }
    public boolean isNotificationsSms() { return notificationsSms; }
    public Instant getCreatedAt() { return createdAt; }

    public void setName(String n) { name = n; }
    public void setEmail(String e) { email = e.toLowerCase(); }
    public void setPasswordHash(String h) { passwordHash = h; }
    public void setRole(Role r) { role = r; }
    public void setNotificationsEmail(boolean b) { notificationsEmail = b; }
    public void setNotificationsSms(boolean b) { notificationsSms = b; }
}
