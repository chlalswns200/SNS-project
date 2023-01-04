package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.CommentRepository;
import com.example.finalproject_choiminjun.repository.LikeRepository;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PostServiceTest {

    PostService postService;
    UserService userService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    CommentRepository commentRepository = mock(CommentRepository.class);
    LikeRepository likeRepository = mock(LikeRepository.class);
    BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);


    @BeforeEach
    public void setUp() {
        postService = new PostService(postRepository, userRepository, commentRepository,likeRepository);
        userService = new UserService(userRepository,bCryptPasswordEncoder);

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

    @Test
    @DisplayName("포스트 1개 조회 - 성공")
    void onePost_success() throws Exception {

        User user = User.builder()
                .id(1L)
                .userName("userName")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .title("title1")
                .body("content1")
                .user(user)
                .build();

        when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        OnePostResponse onePostResponse = postService.get(1L);

        assertEquals(post.getId(),onePostResponse.getId());
        assertEquals(post.getTitle(),onePostResponse.getTitle());
        assertEquals(post.getBody(),onePostResponse.getBody());
        assertEquals(post.getUser().getUserName(),onePostResponse.getUserName());
    }

    @Test
    @DisplayName("포스트 수정 - 실패#1 포스트 존재하지 않음")
    void post_modify_fail() throws Exception {

        PostRequest postRequest = new PostRequest("title1", "body1");


        when(postRepository.findById(any()))
                .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND));
        try {
            postService.modifyOnePost(1L, "userName", postRequest);
        } catch (Exception e) {
            assertEquals(e.getMessage(),"해당 포스트가 없습니다.");
        }
    }

    @Test
    @DisplayName("포스트 수정 - 실패#2 작성자!=유저")
    void post_modify_fail2() throws Exception {

        User userA = User.builder()
                .id(1L)
                .userName("userA")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        User userB = User.builder()
                .id(2L)
                .userName("userB")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(userA)
                .title("title1")
                .body("body1")
                .build();

        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));

        given(userRepository.findByUserName("userA"))
                .willReturn(Optional.of(userA));

        given(userRepository.findByUserName("userB"))
                .willReturn(Optional.of(userB));

        try {
            postService.modifyOnePost(1L, "userB",
                    new PostRequest("title2", "content2"));
        } catch (Exception e) {
            assertEquals("사용자가 권한이 없습니다.",e.getMessage());
        }

    }

    @Test
    @DisplayName("포스트 수정 - 실패#3 유저 존재 하지 않음")
    void post_modify_fail3() throws Exception {

        User userB = User.builder()
                .id(2L)
                .userName("userB")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(userB)
                .title("title1")
                .body("body1")
                .build();

        when(postRepository.findById(any()))
                .thenReturn(Optional.of(post));

        when(userRepository.findByUserName(any()))
                .thenThrow((new AppException(ErrorCode.USERNAME_NOT_FOUND)));

        try {
            postService.modifyOnePost(1L, "userName", new PostRequest("title1", "content1"));

        } catch (Exception e) {
            assertEquals("user를 찾을 수 없습니다.",e.getMessage());
        }
    }

    @Test
    @DisplayName("포스트 삭제 - 실패#1 유저 존재하지 않음")
    void post_delete_fail1() throws Exception {

        User userA = User.builder()
                .id(1L)
                .userName("userA")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(userA)
                .title("title1")
                .body("body1")
                .build();

        given(postRepository.findById(any()))
                .willReturn(Optional.of(post));

        //given
        given(userRepository.findByUserName(any()))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));
        //when
        try {
            postService.deleteOnePost(1L, "userA");
        } catch (Exception e) {
            assertEquals("user를 찾을 수 없습니다.",e.getMessage());
        }
    }

    @Test
    @DisplayName("포스트 삭제 - 실패#2 포스트 존재 하지 않음")
    void post_delete_fail2() throws Exception {

        User userA = User.builder()
                .id(1L)
                .userName("userA")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(userA)
                .title("title1")
                .body("body1")
                .build();

        given(postRepository.findById(any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));
        try {
            postService.deleteOnePost(1L, "userA");
        } catch (Exception e) {
            assertEquals("해당 포스트가 없습니다.",e.getMessage());
        }
    }
    @Test
    @DisplayName("포스트 삭제 - 실패#3 작성자와 유저가 불일치")
    void post_delete_fail3() throws Exception {

        User userA = User.builder()
                .id(1L)
                .userName("userA")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        User userB = User.builder()
                .id(2L)
                .userName("userB")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        Post post = Post.builder()
                .id(1L)
                .user(userA)
                .title("title1")
                .body("body1")
                .build();

        given(postRepository.findById(1L))
                .willReturn(Optional.of(post));

        given(userRepository.findByUserName("userA"))
                .willReturn(Optional.of(userA));

        given(userRepository.findByUserName("userB"))
                .willReturn(Optional.of(userB));

        try {
            postService.deleteOnePost(1L, "userB");
        } catch (Exception e) {
            assertEquals("사용자가 권한이 없습니다.",e.getMessage());
        }
    }

}