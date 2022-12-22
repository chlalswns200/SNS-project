package com.example.finalproject_choiminjun.domain;

import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String title;
    private String body;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public static Post of(PostRequest postRequest) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .body(postRequest.getBody())
                .build();
        return post;
    }

}
