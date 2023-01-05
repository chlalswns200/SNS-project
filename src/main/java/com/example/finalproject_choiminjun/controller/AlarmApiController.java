package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.AlarmResponse;
import com.example.finalproject_choiminjun.service.AlarmService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/v1/alarms")
@RequiredArgsConstructor
public class AlarmApiController {

    private final AlarmService alarmService;
    @GetMapping
    public Response<Page<AlarmResponse>> AlarmList(@PageableDefault(size = 20)
                                                       @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, Authentication authentication) {
        Page<AlarmResponse> alarmResponseList = alarmService.getAlarmList(authentication.getName(),pageable);
        return Response.success(alarmResponseList);
    }
}
