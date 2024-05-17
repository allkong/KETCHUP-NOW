package com.ssafy.double_bean.story.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class StoryReviewEntity {
    private int id;
    private UUID uuid;
    private UUID storyUuid;
    private String storyTitle;
    private UUID userUuid;
    private String userNickname;
    private String title;
    private String content;
    private int score;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public StoryReviewEntity() {
    }

    public StoryReviewEntity(int id, UUID uuid, UUID storyUuid, String storyTitle, UUID userUuid, String userNickname, String title, String content, int score, LocalDateTime createdAt, LocalDateTime modifiedAt) {
        this.id = id;
        this.uuid = uuid;
        this.storyUuid = storyUuid;
        this.storyTitle = storyTitle;
        this.userUuid = userUuid;
        this.userNickname = userNickname;
        this.title = title;
        this.content = content;
        this.score = score;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
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

    public UUID getStoryUuid() {
        return storyUuid;
    }

    public void setStoryUuid(UUID storyUuid) {
        this.storyUuid = storyUuid;
    }

    public String getStoryTitle() {
        return storyTitle;
    }

    public void setStoryTitle(String storyTitle) {
        this.storyTitle = storyTitle;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }

    public String getUserNickname() {
        return userNickname;
    }

    public void setUserNickname(String userNickname) {
        this.userNickname = userNickname;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }

    public void setModifiedAt(LocalDateTime modifiedAt) {
        this.modifiedAt = modifiedAt;
    }

    @Override
    public String toString() {
        return "StoryReviewEntity{" +
                "id=" + id +
                ", uuid=" + uuid +
                ", storyUuid=" + storyUuid +
                ", storyTitle='" + storyTitle + '\'' +
                ", userUuid=" + userUuid +
                ", userNickname='" + userNickname + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", score=" + score +
                ", createdAt=" + createdAt +
                ", modifiedAt=" + modifiedAt +
                '}';
    }
}
