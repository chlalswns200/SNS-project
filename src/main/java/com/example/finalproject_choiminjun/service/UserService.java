package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import com.example.finalproject_choiminjun.domain.dto.UserJoinResponse;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.exception.FinalProjectAppException;
import com.example.finalproject_choiminjun.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public UserJoinResponse join(UserJoinRequest userJoinRequest) {

        String encode = encoder.encode(userJoinRequest.getPassword());

        userRepository.findByUserName(userJoinRequest.getUserName())
                .ifPresent(user ->{
                    throw new FinalProjectAppException(ErrorCode.DUPLICATED_USER_NAME, String.format("UserName:%s", userJoinRequest.getUserName()));
                });

        User save = userRepository.save(userJoinRequest.toEntity(encode));
        return new UserJoinResponse(save.getId(), save.getUserName());
    }

}
