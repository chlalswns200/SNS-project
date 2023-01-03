package com.example.finalproject_choiminjun.service;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.UserRole;
import com.example.finalproject_choiminjun.domain.dto.UserResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.repository.CommentRepository;
import com.example.finalproject_choiminjun.repository.PostRepository;
import com.example.finalproject_choiminjun.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class UserServiceTest {

    PostService postService;
    UserService userService;

    PostRepository postRepository = mock(PostRepository.class);
    UserRepository userRepository = mock(UserRepository.class);

    CommentRepository commentRepository = mock(CommentRepository.class);
    BCryptPasswordEncoder bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);


    @BeforeEach
    public void setUp() {
        postService = new PostService(postRepository, userRepository,commentRepository);
        userService = new UserService(userRepository,bCryptPasswordEncoder);
    }

    @Test
    @DisplayName("권한 변경 - 성공")
    @WithMockUser
    void role_change_success() throws Exception {

        User userAdmin = User.builder()
                .id(1L)
                .userName("user_admin")
                .password("1q2w3e4r!")
                .role(UserRole.ADMIN)
                .build();


        User userNormal = User.builder()
                .id(2L)
                .userName("user_normal")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        given(userRepository.findByUserName("user_admin"))
                .willReturn(Optional.of(userAdmin));

        given(userRepository.findById(2L))
                .willReturn(Optional.of(userNormal));

        userNormal.changeRole(UserRole.ADMIN);
        given(userRepository.saveAndFlush(userNormal))
                .willReturn(userNormal);

        UserResponse userResponse = userService.changeUserRole(2L, "admin", "user_admin");
        assertEquals("권한이 변경되었습니다.",userResponse.getMessage());
        assertEquals(2L,userResponse.getId());

    }

    @Test
    @DisplayName("권한 변경 실패#1 - admin 계정이 db에 없을 경우")
    void role_change_fail1() throws Exception {

        given(userRepository.findByUserName("user_admin"))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));

        try {
            userService.changeUserRole(2L, "admin", "user_admin");
        } catch (Exception e) {
            assertEquals("user를 찾을 수 없습니다.",e.getMessage());
        }

    }

    @Test
    @DisplayName("권한 변경 실패#2 - 권한을 바꿀려는 계정이 db에 없는 경우")
    void role_change_fail2() throws Exception {

        given(userRepository.findById(2l))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));
        try {
            userService.changeUserRole(2L, "admin", "user_admin");
        } catch (Exception e) {
            assertEquals("user를 찾을 수 없습니다.",e.getMessage());
        }
    }


    @Test
    @DisplayName("권한 변경 실패#3 - admin 계정이 아닌 계정으로 권한 변경 시도한 경우")
    void role_change_fail3() throws Exception {

        User userNormal = User.builder()
                .id(1L)
                .userName("user_normal")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        User userNormalB = User.builder()
                .id(2L)
                .userName("user_normal2")
                .password("1q2w3e4r!")
                .role(UserRole.NORMAL)
                .build();

        given(userRepository.findByUserName("user_normal"))
                .willReturn(Optional.of(userNormal));

        given(userRepository.findById(2L))
                .willReturn(Optional.of(userNormalB));

        try {
            UserResponse userResponse = userService.changeUserRole(2L, "admin", "user_normal");
        } catch (Exception e) {
            assertEquals("사용자가 권한이 없습니다.",e.getMessage());
        }
    }

}