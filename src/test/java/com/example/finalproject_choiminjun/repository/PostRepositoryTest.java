package com.example.finalproject_choiminjun.repository;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@DataJpaTest
class PostRepositoryTest {

    @Autowired
    LikeRepository likeRepository;

    @Autowired
    PostRepository postRepository;

    @Test
    @DisplayName("포스트 등록 되는지")
    void post_success() throws Exception {

        User user = User.of(new UserJoinRequest(), "1q2w3e4r!");
        //given
        Post post = Post.builder()
                .id(1L)
                .title("제목입니다")
                .body("내용입니다")
                .user(user)
                .build();
        //when
        postRepository.save(post);

        //then
        Optional<Post> byId = postRepository.findById(1L);
        Post post1 = byId.get();

        assertEquals(post1.getId(),1L);
        assertEquals(post1.getTitle(),"제목입니다");

    }

}