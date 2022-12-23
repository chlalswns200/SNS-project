package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.domain.dto.UserLoginRequest;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.service.PostService;
import com.example.finalproject_choiminjun.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PostService postService;

    @Test
    @DisplayName("포스트 - 성공")
    @WithMockUser()
    void post_success() throws Exception {

        PostRequest postRequest = new PostRequest("title1", "body1");
        PostResponse postResponse = new PostResponse("게시글 작성 성공!!", 0l);

        when(postService.post(any(), any()))
                .thenReturn(postResponse);

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists());

    }

    @Test
    @DisplayName("포스트 - 인증 실패 #1 인증 되지 않은 유저")
    @WithAnonymousUser
    void post_fail1() throws Exception {

        PostRequest postRequest = new PostRequest("title1", "body1");

        when(postService.post(any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("전체 포스트 리스트 - 성공")
    @WithMockUser
    void pages() throws Exception {

        List<OnePostResponse> postList = new ArrayList<>();

        Page<OnePostResponse> pages = new PageImpl<>(postList);
        //given
        when(postService.getPostList(any()))
                .thenReturn(pages);
        //when
        mockMvc.perform(get("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
        //then
    }

}
