package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Comment;
import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostApiController {
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
        OnePostResponse onePostResponse = postService.get(id);
        return Response.success(onePostResponse);
    }

    @PutMapping("/{id}")
    public Response<PostResponse> modifyOne(@PathVariable Long id, Authentication authentication, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.modifyOnePost(id, authentication.getName(), postRequest);
        return Response.success(postResponse);
    }

    @DeleteMapping("/{id}")
    public Response<PostResponse> deleteOne(@PathVariable Long id, Authentication authentication) {
        PostResponse postResponse = postService.deleteOnePost(id, authentication.getName());
        return Response.success(postResponse);
    }

    @GetMapping
    public Response<Page<OnePostResponse>> postList(@PageableDefault(size = 20)
                                                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OnePostResponse> pages = postService.getPostList(pageable);
        return Response.success(pages);
    }

    @GetMapping("/my")
    public Response<Page<OnePostResponse>> my(@PageableDefault(size = 20)
                                                  @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            , Authentication authentication) {

        Page<OnePostResponse> onePostResponses = postService.myPost(authentication.getName(), pageable);
        return Response.success(onePostResponses);
    }

    @PostMapping("/{postsId}/comments")
    public Response<CommentResponse> comments(@PathVariable Long postsId, Authentication authentication, @RequestBody CommentRequest commentRequest) {

        CommentResponse commentResponse = postService.writeComment(postsId, authentication.getName(), commentRequest);
        return Response.success(commentResponse);

    }

    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> commentsList(@PageableDefault(size = 10)
                                                            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,@PathVariable Long postId) {
        Page<CommentResponse> commentsList =postService.getCommentsList(pageable,postId);
        return Response.success(commentsList);
    }

    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentModifyResponse> commentModify(@PathVariable Long postId,@PathVariable Long id,
                                                   Authentication authentication,@RequestBody CommentRequest commentRequest) {
        CommentModifyResponse commentModifyResponse = postService.modifyComment(postId, id, authentication.getName(), commentRequest);
        return Response.success(commentModifyResponse);
    }

    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> commentDelete(@PathVariable Long postId,@PathVariable Long id,
                                                         Authentication authentication) {
        CommentDeleteResponse commentDeleteResponse = postService.deleteOneComment(postId,id,authentication.getName());
        return Response.success(commentDeleteResponse);
    }




}
