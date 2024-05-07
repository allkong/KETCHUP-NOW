package com.ssafy.double_bean.user.service;

import com.ssafy.double_bean.common.exception.ErrorCode;
import com.ssafy.double_bean.common.exception.HttpResponseException;
import com.ssafy.double_bean.user.dto.SignUpRequestDto;
import com.ssafy.double_bean.user.model.entity.UserEntity;
import com.ssafy.double_bean.user.model.repository.UserRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public void doSignUp(SignUpRequestDto dto) {
        UserEntity requestEntity = dto.toRequestEntity();
        requestEntity.setSalt(BCrypt.gensalt());
        requestEntity.setPassword(BCrypt.hashpw(requestEntity.getPassword(), requestEntity.getSalt()));

        try {
            userRepository.insert(requestEntity);
        } catch (DuplicateKeyException exception) {
            throw new HttpResponseException(ErrorCode.DUPLICATED_FIELD_VALUE);
        }
    }

    @Override
    public Optional<UserEntity> findUserByLoginId(String loginId) {
        return userRepository.findByLoginId(loginId);
    }

    @Override
    public Optional<UserEntity> findUserByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid);
    }
}
