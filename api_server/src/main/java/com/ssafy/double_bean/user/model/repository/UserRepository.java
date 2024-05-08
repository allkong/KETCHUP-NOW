package com.ssafy.double_bean.user.model.repository;

import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.util.type_handler.UUIDTypeHandler;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Mapper
public interface UserRepository {
    @Select("SELECT u.id, uuid, login_id, password, salt, nickname, created_at, u.modified_at "
            + "FROM users u LEFT JOIN passwords p ON u.id=p.user_id")
    @Results(id = "userResult", value = {@Result(property = "id", column = "id"),
            @Result(property = "uuid", column = "uuid", typeHandler = UUIDTypeHandler.class),
            @Result(property = "loginId", column = "login_id"), @Result(property = "password", column = "password"),
            @Result(property = "salt", column = "salt"), @Result(property = "nickname", column = "nickname"),
            @Result(property = "createdAt", column = "created_at"),
            @Result(property = "modifiedAt", column = "modified_at"),})
    List<UserEntity> getAll();

    @Select("SELECT u.id, uuid, login_id, password, salt, nickname, created_at, u.modified_at "
            + "FROM users u LEFT JOIN passwords p ON u.id=p.user_id WHERE login_id=#{loginId}")
    @ResultMap("userResult")
    Optional<UserEntity> findByLoginId(String loginId);

    @Select("SELECT u.id, uuid, login_id, password, salt, nickname, created_at, u.modified_at "
            + "FROM users u LEFT JOIN passwords p ON u.id=p.user_id WHERE u.id=#{id}")
    @ResultMap("userResult")
    Optional<UserEntity> findById(int id);

    @Select("SELECT u.id, uuid, login_id, password, salt, nickname, created_at, u.modified_at "
            + "FROM users u LEFT JOIN passwords p ON u.id=p.user_id WHERE uuid='${uuid}'")
    @ResultMap("userResult")
    Optional<UserEntity> findByUuid(@Param("uuid") UUID uuid);

    @Insert({"INSERT INTO users(login_id, nickname) VALUES (#{loginId}, #{nickname}); ",
            "INSERT INTO passwords(user_id, password, salt) VALUES (LAST_INSERT_ID(), #{password}, #{salt});"})
    void insert(UserEntity dto);
}
