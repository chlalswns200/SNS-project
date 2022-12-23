package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Post;
import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.OnePostResponse;
import com.example.finalproject_choiminjun.domain.dto.PostRequest;
import com.example.finalproject_choiminjun.domain.dto.PostResponse;
import com.example.finalproject_choiminjun.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Pageable;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    private final PostService postService;

    @PostMapping
    public Response<PostResponse> posts(@RequestBody PostRequest postRequest, Authentication authentication) {

        String userName = authentication.getName();
        log.info("name = {}", userName);

        PostResponse postResponse = postService.post(postRequest, userName);
        return Response.success(postResponse);
    }

    @GetMapping("/{id}")
    public Response<OnePostResponse> getOnePost(@PathVariable Long id) {
        Post post = postService.get(id);
        return Response.success(new OnePostResponse().entityToResponse(post));
    }

    @PutMapping("/{id}")
    public Response<PostResponse> modifyOne(@PathVariable Long id, Authentication authentication, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.modifyOnePost(id, authentication.getName(), postRequest);
        return Response.success(postResponse);
    }

    @DeleteMapping("/{id}")
    public Response<PostResponse> deleteOne(@PathVariable Long id, Authentication authentication, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.deleteOnePost(id, authentication.getName(), postRequest);
        return Response.success(postResponse);
    }
}
