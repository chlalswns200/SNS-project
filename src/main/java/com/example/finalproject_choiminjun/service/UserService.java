package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.repository.UserRepository;
import com.example.finalproject_choiminjun.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    private long expireTimeMs = 1000 * 60 * 60;

    @Value("${jwt.token.secret}")
    private String secretKey;

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {

        String encode = encoder.encode(userJoinRequest.getPassword());

        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user ->{
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME);
                });

        User save = userRepository.save(User.of(userJoinRequest,encode));
        return new UserJoinResponse(save.getId(), save.getUserName());
    }


    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }

    public String login(UserLoginRequest userLoginRequest) {
        User byUserName = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if(!encoder.matches(userLoginRequest.getPassword(), byUserName.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }

        return JwtTokenUtil.generateToken(userLoginRequest.getUserName(), secretKey, expireTimeMs);
    }

    public UserResponse changeUserRole(Long id, String role, String name) {

        User byUserName = userRepository.findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        if (!byUserName.getRole().equals(UserRole.ADMIN)) {
            throw new AppException(ErrorCode.INVALID_PERMISSION);
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        user.changeRole(UserRole.valueOf(role.toUpperCase()));

        User userRoleChanged = userRepository.saveAndFlush(user);

        return new UserResponse("권한이 변경되었습니다.", userRoleChanged.getId());
    }
}
