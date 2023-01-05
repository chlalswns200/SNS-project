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

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    public Page<AlarmResponse> getAlarmList(String name, Pageable pageable) {

        User user = userRepository.findByUserName(name)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND));

        Page<Alarm> allByTargetId = alarmRepository.findAllByTargetId(user.getId(), pageable);

        Page<AlarmResponse> alarmResponses = AlarmResponse.makeResponse(allByTargetId);

        return alarmResponses;
    }
}
