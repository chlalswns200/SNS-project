package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.User;
import com.example.finalproject_choiminjun.domain.dto.UserJoinRequest;
import com.example.finalproject_choiminjun.domain.dto.UserJoinResponse;
import com.example.finalproject_choiminjun.domain.dto.UserLoginRequest;
import com.example.finalproject_choiminjun.domain.dto.UserLoginResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @Test
    @DisplayName("회원가입 - 성공")
    @WithMockUser(username = "chlalswns200")
    void join_success() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .password("password")
                .build();
        UserJoinResponse userJoinResponse = new UserJoinResponse(0L, userJoinRequest.getUserName());

        when(userService.join(any()))
                .thenReturn(userJoinResponse);

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 - 실패 #1 유저이름 중복")
    @WithMockUser(username = "chlalswns200")
    void join_fail() throws Exception {

        UserJoinRequest userJoinRequest = UserJoinRequest.builder()
                .userName("chlalswns200")
                .password("password")
                .build();

        when(userService.join(any()))
                .thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME));

        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("로그인 - 성공")
    @WithMockUser(username = "chlalswns200",password = "1q2w3e4r!")
    void login_success() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200", "1q2w3e4r!");

        when(userService.login(userLoginRequest))
                .thenReturn("token1234");

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 - 실패 #1 해당 유저 없음")
    @WithMockUser(username = "chlalswns200",password = "1q2w3e4r!")
    void login_fail_no_user() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200", "1q2w3e4r!");

        when(userService.login(any()))
                .thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("로그인 - 실패 #2 비밀번호 없음")
    @WithMockUser(username = "chlalswns200",password = "1q2w3e4r!")
    void login_fail_invalid_password() throws Exception {

        UserLoginRequest userLoginRequest = new UserLoginRequest("chlalswns200", "1q2w3e4r!");

        when(userService.login(any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PASSWORD));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



}