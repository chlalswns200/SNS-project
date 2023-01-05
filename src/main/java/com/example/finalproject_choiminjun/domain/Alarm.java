package com.example.finalproject_choiminjun.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class Alarm extends BaseEntity{
    @Id
    @GeneratedValue
    private Long id;
    private String alarmType;
    private LocalDateTime deletedAt;
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;
    private Long fromUserId;
    private Long targetId;
    private String text;

    public static Alarm commentAlarm(Post post, User user) {
        Alarm newCommentOnPost = Alarm.builder()
                .alarmType("NEW_COMMENT_ON_POST")
                .text("new comment!")
                .fromUserId(user.getId())
                .targetId(post.getUser().getId())
                .user(post.getUser())
                .build();
        return newCommentOnPost;
    }

    public static Alarm likeAlarm(Post post, User user) {
        Alarm newCommentOnPost = Alarm.builder()
                .alarmType("NEW_LIKE_ON_POST")
                .text("new like!")
                .fromUserId(user.getId())
                .targetId(post.getUser().getId())
                .user(post.getUser())
                .build();
        return newCommentOnPost;
    }

}
