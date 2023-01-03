package com.example.finalproject_choiminjun.domain.dto;

import com.example.finalproject_choiminjun.domain.Comment;
import com.example.finalproject_choiminjun.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private String comment;
    private String userName;
    private Long postId;
    private LocalDateTime createdAt;

    public static Page<CommentResponse> toList(Page<Comment> comments) {
        Page<CommentResponse> map = comments.map(m -> CommentResponse.builder()
                .id(m.getId())
                .comment(m.getComment())
                .userName(m.getUser().getUserName())
                .postId(m.getPost().getId())
                .createdAt(m.getCreatedAt())
                .build());
        return map;
    }

}
