package com.ssafy.double_bean.user.model.entity;

import java.time.LocalDateTime;
import java.util.UUID;

public class UserEntity {
	// Identifiers
	private int id;
	private UUID uuid;
	// Authentication
	private String loginId;
	private String password;
	private String salt;
	// Business
	private String nickname;
	// Management
	private LocalDateTime createdAt;
	private LocalDateTime modifiedAt;

	public UserEntity() {
	}

	public UserEntity(int id, UUID uuid, String loginId, String password, String salt, String nickname,
			LocalDateTime createdAt, LocalDateTime modifiedAt) {
		this.id = id;
		this.uuid = uuid;
		this.loginId = loginId;
		this.password = password;
		this.salt = salt;
		this.nickname = nickname;
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

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
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
		return "UserEntity [id=" + id + ", uuid=" + uuid + ", loginId=" + loginId + ", password=" + password + ", salt="
				+ salt + ", nickname=" + nickname + ", createdAt=" + createdAt + ", modifiedAt=" + modifiedAt + "]";
	}
}
