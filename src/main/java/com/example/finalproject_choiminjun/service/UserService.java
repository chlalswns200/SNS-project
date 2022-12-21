package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import com.example.finalproject_choiminjun.domain.dto.UserJoinResponse;
import com.example.finalproject_choiminjun.domain.dto.UserLoginRequest;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.repository.UserRepository;
import com.example.finalproject_choiminjun.utils.JwtTokenUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
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
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("UserName:%s", userJoinRequest.getUserName()));
                });

        User save = userRepository.save(userJoinRequest.toEntity(encode));
        return new UserJoinResponse(save.getId(), save.getUserName());
    }


    public User getUserByUserName(String userName) {
        return userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, userName+ "이 없습니다."));
    }

    public String login(UserLoginRequest userLoginRequest) {
        User byUserName = userRepository.findByUserName(userLoginRequest.getUserName())
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userLoginRequest.getUserName()+"이 없습니다."));

        if(!encoder.matches(userLoginRequest.getPassword(), byUserName.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD,"패스워드가 잘못 되었습니다.");
        }

        return JwtTokenUtil.generateToken(userLoginRequest.getUserName(), secretKey, expireTimeMs);
    }
}
