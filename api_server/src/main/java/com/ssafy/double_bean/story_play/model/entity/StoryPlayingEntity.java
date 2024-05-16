package com.ssafy.double_bean.story_play.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoryPlayingEntity {
    private int id;
    private UUID uuid;
    private int storyId;
    private UUID storyUuid;
    private int playerId;
    private UUID playerUuid;
    private LocalDateTime createdAt;
    private LocalDateTime clearedAt;

    public StoryPlayingEntity() {
    }

    public StoryPlayingEntity(int id, UUID uuid, int storyId, UUID storyUuid, int playerId, UUID playerUuid) {
        this(id, uuid, storyId, storyUuid, playerId, playerUuid, null, null);
        this.createdAt = null;
        this.clearedAt = null;
    }

    public StoryPlayingEntity(int id, UUID uuid, int storyId, UUID storyUuid, int playerId, UUID playerUuid, LocalDateTime createdAt, LocalDateTime clearedAt) {
        this.id = id;
        this.uuid = uuid;
        this.storyId = storyId;
        this.storyUuid = storyUuid;
        this.playerId = playerId;
        this.playerUuid = playerUuid;
        this.createdAt = createdAt;
        this.clearedAt = clearedAt;
    }

    public static StoryPlayingEntity getStartRequestEntity(int storyId, UUID playerUuid) {
        StoryPlayingEntity emptyEntity = new StoryPlayingEntity();
        emptyEntity.setStoryId(storyId);
        emptyEntity.setPlayerUuid(playerUuid);
        return emptyEntity;
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

    public int getPlayerId() {
        return playerId;
    }

    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public void setPlayerUuid(UUID playerUuid) {
        this.playerUuid = playerUuid;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getClearedAt() {
        return clearedAt;
    }

    public void setClearedAt(LocalDateTime clearedAt) {
        this.clearedAt = clearedAt;
    }

    @Override
    public String toString() {
        return "StoryPlayEntity{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", storyId=" + storyId +
                ", storyUuid=" + storyUuid +
                ", playerId=" + playerId +
                ", playerUuid=" + playerUuid +
                ", createdAt=" + createdAt +
                ", clearedAt=" + clearedAt +
                '}';
    }
}
