package com.ssafy.double_bean.story.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.ssafy.double_bean.user.model.entity.UserEntity;

public class StoryBaseEntity {
	private int id;
	private UUID uuid;
	private UserEntity author;
	private LocalDateTime createdAt;

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

	public UserEntity getAuthor() {
		return author;
	}

	public void setAuthor(UserEntity author) {
		this.author = author;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}
}
