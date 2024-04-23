package com.ssafy.double_bean.user.model.entity;

public class UserEntityBuilder {
	public static UserEntity getForInsert(String loginId, String nickname) {
		UserEntity entity = new UserEntity();
		entity.setLoginId(loginId);
		entity.setNickname(nickname);
		return entity;
	}

	public static UserEntity getForPassword(String hashedPassword, String salt) {
		UserEntity entity = new UserEntity();
		entity.setPassword(hashedPassword);
		entity.setSalt(salt);
		return entity;
	}

	public static UserEntity getForInsert(String loginId, String hashedPassword, String salt, String nickname) {
		UserEntity entity = new UserEntity();
		entity.setLoginId(loginId);
		entity.setPassword(hashedPassword);
		entity.setSalt(salt);
		entity.setNickname(nickname);
		return entity;
	}
}
