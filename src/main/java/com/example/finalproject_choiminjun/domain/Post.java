package com.example.finalproject_choiminjun.domain;

import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import lombok.*;

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

    public static Post of(PostRequest postRequest,User user) {
        Post post = Post.builder()
                .title(postRequest.getTitle())
                .body(postRequest.getBody())
                .user(user)
                .build();
        return post;
    }

    public void modifyPost(String title, String body) {
        this.title = title;
        this.body = body;
    }

}
