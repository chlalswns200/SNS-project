package com.example.finalproject_choiminjun.domain.dto;

import com.example.finalproject_choiminjun.domain.Alarm;
import com.example.finalproject_choiminjun.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Builder
@NoArgsConstructor
public class OnePostResponse {
    private Long id;
    private String title;
    private String body;
    private String userName;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;



    public static Page<OnePostResponse> makeResponse(Page<Post> allByUser) {
        Page<OnePostResponse> postResponses = allByUser.map(m -> OnePostResponse.builder()
                .id(m.getId())
                .title(m.getTitle())
                .body(m.getBody())
                .userName(m.getUser().getUserName())
                .createdAt(m.getCreatedAt())
                .lastModifiedAt(m.getLastModifiedAt())
                .build());
        return postResponses;
    }
}
