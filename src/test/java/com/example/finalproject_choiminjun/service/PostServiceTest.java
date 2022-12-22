package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    PostService postService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);


    @BeforeEach
    public void setUp() {
        postService = new PostService(postRepository, userRepository);

    }

    @Test
    @DisplayName("등록 성공")
    public void post_success() {

        Post mockPost = mock(Post.class);
        User mockUser = mock(User.class);

        when(userRepository.findByUserName("userA"))
                .thenReturn(Optional.of(mockUser));

        when(postRepository.save(any()))
                .thenReturn(mockPost);

        PostRequest postRequest = new PostRequest("title","content");
        PostResponse post = postService.post(postRequest, "userA");
        assertEquals(post.getPostId(),0L);
    }
    @Test
    @DisplayName("등록 실패 - 찾는 username 없음")
    public void post_fail() {

        Post mockPost = mock(Post.class);

        when(userRepository.findByUserName("userA"))
                .thenReturn(Optional.empty());

        when(postRepository.save(any()))
                .thenReturn(mockPost);

        PostRequest postRequest = new PostRequest("title","content");
        assertThrows(AppException.class, () -> {
            postService.post(postRequest, "userB");
        });
    }
}