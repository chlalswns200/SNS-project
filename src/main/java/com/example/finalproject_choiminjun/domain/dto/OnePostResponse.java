package com.example.finalproject_choiminjun.domain.dto;

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

    public static OnePostResponse entityToResponse(Post post) {

        OnePostResponse opr = OnePostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
        return opr;
    }

    public static Page<OnePostResponse> toList(Page<Post> all) {

        List<OnePostResponse> postList = new ArrayList<>();

        for (Post post : all) {
            OnePostResponse onePostResponse = OnePostResponse.entityToResponse(post);
            postList.add(onePostResponse);
        }
        Page<OnePostResponse> postPage = new PageImpl<>(postList);
        return postPage;
    }
}
