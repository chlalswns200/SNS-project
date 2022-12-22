package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostController {
    private final PostService postService;
    @PostMapping
    public Response<PostResponse> posts(@RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.post(postRequest);
        return Response.success(postResponse);
    }


}
