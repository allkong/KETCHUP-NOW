package com.ssafy.double_bean.story.model.entity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

public class SpotEntity {
    private int id;
    private UUID uuid;
    private double latitude;
    private double longitude;
    private double orderIndex;
    private String title;
    private String description;
    private URI imageUri;
    private URI thumbnailImageUri;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    // 이벤트 관련 ========================================
    private EventType eventType;
    private URI eventImageUri;
    private URI eventThumbnailImageUri;
    private String jsonEventContent;

    public SpotEntity() {
    }

    public SpotEntity(int id, UUID uuid, double latitude, double longitude, double orderIndex, String title, String description, URI imageUri, URI thumbnailImageUri, LocalDateTime createdAt, LocalDateTime modifiedAt, EventType eventType, URI eventImageUri, URI eventThumbnailImageUri, String jsonEventContent) {
        this.id = id;
        this.uuid = uuid;
        this.latitude = latitude;
        this.longitude = longitude;
        this.orderIndex = orderIndex;
        this.title = title;
        this.description = description;
        this.imageUri = imageUri;
        this.thumbnailImageUri = thumbnailImageUri;
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.eventType = eventType;
        this.eventImageUri = eventImageUri;
        this.eventThumbnailImageUri = eventThumbnailImageUri;
        this.jsonEventContent = jsonEventContent;
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

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(double orderIndex) {
        this.orderIndex = orderIndex;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public URI getImageUri() {
        return imageUri;
    }

    public void setImageUri(URI imageUri) {
        this.imageUri = imageUri;
    }

    public URI getThumbnailImageUri() {
        return thumbnailImageUri;
    }

    public void setThumbnailImageUri(URI thumbnailImageUri) {
        this.thumbnailImageUri = thumbnailImageUri;
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

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public URI getEventImageUri() {
        return eventImageUri;
    }

    public void setEventImageUri(URI eventImageUri) {
        this.eventImageUri = eventImageUri;
    }

    public URI getEventThumbnailImageUri() {
        return eventThumbnailImageUri;
    }

    public void setEventThumbnailImageUri(URI eventThumbnailImageUri) {
        this.eventThumbnailImageUri = eventThumbnailImageUri;
    }

    public String getJsonEventContent() {
        return jsonEventContent;
    }

    public void setJsonEventContent(String jsonEventContent) {
        this.jsonEventContent = jsonEventContent;
    }

    public enum EventType {
        NARRATIVE, QUIZ, PHOTO_MISSION,
    }
}
