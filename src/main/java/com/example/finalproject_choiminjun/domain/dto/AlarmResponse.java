package com.example.finalproject_choiminjun.domain.dto;

import com.example.finalproject_choiminjun.domain.Alarm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AlarmResponse {
    private Long id;
    private String alarmType;
    private Long fromUserId;
    private Long targetId;
    private String text;
    private LocalDateTime createdAt;

    public static Page<AlarmResponse> makeResponse(Page<Alarm> allByTargetId) {

        Page<AlarmResponse> alarmResponses = allByTargetId.map(m->AlarmResponse.builder()
                .id(m.getId())
                .alarmType(m.getAlarmType())
                .fromUserId(m.getFromUserId())
                .targetId(m.getTargetId())
                .text(m.getText())
                .createdAt(m.getCreatedAt())
                .build());

        return alarmResponses;
    }
}
