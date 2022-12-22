package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private final PostService postService;
    @PostMapping
    public Response<PostResponse> posts(@RequestBody PostRequest postRequest, Authentication authentication) {

        String userName = authentication.getName();
        log.info("name = {}",userName);

        PostResponse postResponse = postService.post(postRequest,userName);
        return Response.success(postResponse);
    }


}
