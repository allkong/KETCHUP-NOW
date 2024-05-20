package com.ssafy.double_bean.story_play.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

// 스팟 클리어 기록
public class StoryPlayingLogEntity {
    private int id;
    private UUID uuid;
    // 연결된 플레이 기록
    private int storyPlayingId;
    private UUID storyPlayingUuid;
    // 클리어한 스팟
    private int spotId;
    private UUID spotUuid;
    // 클리어한 사용자
    private int userId;
    private UUID userUuid;
    // 사용자 답변
    private String jsonEventContent;
    private LocalDateTime createdAt;

    public StoryPlayingLogEntity() {
    }

    public StoryPlayingLogEntity(int id, UUID uuid, int storyPlayingId, UUID storyPlayingUuid, int spotId, UUID spotUuid, int userId, UUID userUuid, String jsonEventContent, LocalDateTime createdAt) {
        this.id = id;
        this.uuid = uuid;
        this.storyPlayingId = storyPlayingId;
        this.storyPlayingUuid = storyPlayingUuid;
        this.spotId = spotId;
        this.spotUuid = spotUuid;
        this.userId = userId;
        this.userUuid = userUuid;
        this.jsonEventContent = jsonEventContent;
        this.createdAt = createdAt;
    }

    public static StoryPlayingLogEntity getCreateRequestEntity(UUID userUuid, UUID storyPlayingUuid, UUID spotUuid) {
        StoryPlayingLogEntity entity = new StoryPlayingLogEntity();
        entity.setUserUuid(userUuid);
        entity.setStoryPlayingUuid(storyPlayingUuid);
        entity.setSpotUuid(spotUuid);
        return entity;
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

    public int getStoryPlayingId() {
        return storyPlayingId;
    }

    public void setStoryPlayingId(int storyPlayingId) {
        this.storyPlayingId = storyPlayingId;
    }

    public UUID getStoryPlayingUuid() {
        return storyPlayingUuid;
    }

    public void setStoryPlayingUuid(UUID storyPlayingUuid) {
        this.storyPlayingUuid = storyPlayingUuid;
    }

    public int getSpotId() {
        return spotId;
    }

    public void setSpotId(int spotId) {
        this.spotId = spotId;
    }

    public UUID getSpotUuid() {
        return spotUuid;
    }

    public void setSpotUuid(UUID spotUuid) {
        this.spotUuid = spotUuid;
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

    public String getJsonEventContent() {
        return jsonEventContent;
    }

    public void setJsonEventContent(String jsonEventContent) {
        this.jsonEventContent = jsonEventContent;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "StoryPlayingLogEntity{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", storyPlayingId=" + storyPlayingId +
                ", storyPlayingUuid=" + storyPlayingUuid +
                ", spotId=" + spotId +
                ", spotUuid=" + spotUuid +
                ", userId=" + userId +
                ", userUuid=" + userUuid +
                ", jsonEventContent='" + jsonEventContent + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
