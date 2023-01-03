package com.example.finalproject_choiminjun.domain;

import com.example.finalproject_choiminjun.domain.dto.CommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import javax.persistence.*;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseEntity{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String comment;
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    public static Comment of(String comment, Post post, User user) {
        Comment commentOne = builder()
                .comment(comment)
                .post(post)
                .user(user)
                .build();
        return commentOne;
    }

}
