package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.Alarm;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.AlarmResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final UserRepository userRepository;

    private User findUser(Optional<User> userRepository) {
        return userRepository
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));
    }
    public Page<AlarmResponse> getAlarmList(String name, Pageable pageable) {

        User user = findUser(userRepository.findByUserName(name));

        Page<Alarm> allByTargetId = alarmRepository.findAllByTargetId(user.getId(), pageable);

        Page<AlarmResponse> alarmResponses = AlarmResponse.makeResponse(allByTargetId);

        return alarmResponses;
    }
}
