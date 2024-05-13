package com.ssafy.double_bean.story.model.entity;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

import com.ssafy.double_bean.story.dto.StoryResponseDto;

public class StoryEntity {
    private int id;
    private UUID uuid;
    private UUID storyBaseUuid;
    private String authorNickname;
    private UUID authorUuid;
    private int version;
    private StoryStatus status;
    private String title;
    private String description;
    private String sido;
    private String gungu;
    private URI imageUri;
    private URI thumbnailImageUri;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public StoryEntity(int id, UUID uuid, UUID storyBaseUuid, String authorNickname, UUID authorUuid, int version,
			StoryStatus status, String title, String description, String sido, String gungu, URI imageUri,
			URI thumbnailImageUri, LocalDateTime createdAt, LocalDateTime modifiedAt) {
		super();
		this.id = id;
		this.uuid = uuid;
		this.storyBaseUuid = storyBaseUuid;
		this.authorNickname = authorNickname;
		this.authorUuid = authorUuid;
		this.version = version;
		this.status = status;
		this.title = title;
		this.description = description;
		this.sido = sido;
		this.gungu = gungu;
		this.imageUri = imageUri;
		this.thumbnailImageUri = thumbnailImageUri;
		this.createdAt = createdAt;
		this.modifiedAt = modifiedAt;
	}

	public StoryEntity() {
    }

    public StoryResponseDto toResponseDto() {
        return new StoryResponseDto(uuid, storyBaseUuid, authorNickname, authorUuid, version, status, title, description, sido,
                gungu, imageUri, thumbnailImageUri, createdAt, modifiedAt);
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
    
    public UUID getStoryBaseUuid() {
		return storyBaseUuid;
	}

	public void setStoryBaseUuid(UUID storyBaseUuid) {
		this.storyBaseUuid = storyBaseUuid;
	}
	
	public String getAuthorNickname() {
		return authorNickname;
	}

	public void setAuthorNickname(String authorNickname) {
		this.authorNickname = authorNickname;
	}

	public UUID getAuthorUuid() {
		return authorUuid;
	}

	public void setAuthorUuid(UUID authorUuid) {
		this.authorUuid = authorUuid;
	}

	public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public StoryStatus getStatus() {
        return status;
    }

    public void setStatus(StoryStatus status) {
        this.status = status;
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

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    public String getGungu() {
        return gungu;
    }

    public void setGungu(String gungu) {
        this.gungu = gungu;
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
    
	@Override
	public String toString() {
		return "StoryEntity [id=" + id + ", uuid=" + uuid + ", storyBaseUuid=" + storyBaseUuid + ", authorNickname="
				+ authorNickname + ", authorUuid=" + authorUuid + ", version=" + version + ", status=" + status
				+ ", title=" + title + ", description=" + description + ", sido=" + sido + ", gungu=" + gungu
				+ ", imageUri=" + imageUri + ", thumbnailImageUri=" + thumbnailImageUri + ", createdAt=" + createdAt
				+ ", modifiedAt=" + modifiedAt + "]";
	}

	public enum StoryStatus {
        WRITING, PUBLISHED
    }
}
