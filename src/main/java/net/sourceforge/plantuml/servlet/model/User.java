package net.sourceforge.plantuml.servlet.model;

import java.sql.Timestamp;

/**
 * 用户实体类.
 */
public class User {
    private Long id;
    private String username;
    private String email;
    private String passwordHash;
    private String fullName;
    private String avatarUrl;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private Timestamp lastLogin;
    private Boolean isActive;

    public User() {
    }

    public User(final String newUsername, final String newEmail, final String newPasswordHash) {
        this.username = newUsername;
        this.email = newEmail;
        this.passwordHash = newPasswordHash;
        this.isActive = true;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(final Long newId) {
        this.id = newId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(final String newUsername) {
        this.username = newUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String newEmail) {
        this.email = newEmail;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(final String newPasswordHash) {
        this.passwordHash = newPasswordHash;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(final String newFullName) {
        this.fullName = newFullName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(final String newAvatarUrl) {
        this.avatarUrl = newAvatarUrl;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Timestamp newCreatedAt) {
        this.createdAt = newCreatedAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(final Timestamp newUpdatedAt) {
        this.updatedAt = newUpdatedAt;
    }

    public Timestamp getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(final Timestamp newLastLogin) {
        this.lastLogin = newLastLogin;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(final Boolean newIsActive) {
        this.isActive = newIsActive;
    }
}

