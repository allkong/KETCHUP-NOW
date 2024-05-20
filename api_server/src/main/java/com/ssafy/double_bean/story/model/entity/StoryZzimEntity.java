package com.ssafy.double_bean.story.model.entity;


import java.time.LocalDateTime;
import java.util.UUID;

public class StoryZzimEntity {
    private int id;
    private UUID uuid;
    private int storyId;
    private UUID storyUuid;
    private int userId;
    private UUID userUuid;
    private LocalDateTime createdAt;

    public StoryZzimEntity() {
    }

    public StoryZzimEntity(int id, UUID uuid, int storyId, UUID storyUuid, int userId, UUID userUuid, LocalDateTime createdAt) {
        this.id = id;
        this.uuid = uuid;
        this.storyId = storyId;
        this.storyUuid = storyUuid;
        this.userId = userId;
        this.userUuid = userUuid;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public int getStoryId() {
        return storyId;
    }

    public void setStoryId(int storyId) {
        this.storyId = storyId;
    }

    public UUID getStoryUuid() {
        return storyUuid;
    }

    public void setStoryUuid(UUID storyUuid) {
        this.storyUuid = storyUuid;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StoryZzimEntity{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", storyId=" + storyId +
                ", storyUuid=" + storyUuid +
                ", userId=" + userId +
                ", userUuid=" + userUuid +
                ", createdAt=" + createdAt +
                '}';
    }
}
