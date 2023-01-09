package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Comment;
import com.example.finalproject_choiminjun.domain.Response;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.service.PostService;
import io.swagger.annotations.ApiOperation;
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

    @ApiOperation(value = "글 작성",notes = "로그인한 계정으로 게시글을 작성 한다")
    @PostMapping
    public Response<PostResponse> posts(@RequestBody PostRequest postRequest, Authentication authentication) {

        String userName = authentication.getName();
        log.info("name = {}", userName);

        PostResponse postResponse = postService.post(postRequest, userName);
        return Response.success(postResponse);
    }

    @ApiOperation(value = "게시글 조회",notes = "게시글 1개를 조회 한다")
    @GetMapping("/{id}")
    public Response<OnePostResponse> getOnePost(@PathVariable Long id) {
        OnePostResponse onePostResponse = postService.get(id);
        return Response.success(onePostResponse);
    }

    @ApiOperation(value = "게시글 수정",notes = "기존 작성한 게시글을 수정 한다")
    @PutMapping("/{id}")
    public Response<PostResponse> modifyOne(@PathVariable Long id, Authentication authentication, @RequestBody PostRequest postRequest) {
        PostResponse postResponse = postService.modifyOnePost(id, authentication.getName(), postRequest);
        return Response.success(postResponse);
    }

    @ApiOperation(value = "게시글 삭제",notes = "기존 작성한 게시글을 삭제 한다")
    @DeleteMapping("/{id}")
    public Response<PostResponse> deleteOne(@PathVariable Long id, Authentication authentication) {
        PostResponse postResponse = postService.deleteOnePost(id, authentication.getName());
        return Response.success(postResponse);
    }

    @ApiOperation(value = "게시글 목록 조회",notes = "전체 게시글을 20개씩 조회 한다")
    @GetMapping
    public Response<Page<OnePostResponse>> postList(@PageableDefault(size = 20)
                                                    @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<OnePostResponse> pages = postService.getPostList(pageable);
        return Response.success(pages);
    }

    @ApiOperation(value = "마이 피드 조회",notes = "마이 피드를 조회 한다")
    @GetMapping("/my")
    public Response<Page<OnePostResponse>> my(@PageableDefault(size = 20)
                                                  @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
            , Authentication authentication) {

        Page<OnePostResponse> onePostResponses = postService.myPost(authentication.getName(), pageable);
        return Response.success(onePostResponses);
    }

    @ApiOperation(value = "댓글 작성",notes = "특정 게시글에 댓글을 작성한다")
    @PostMapping("/{postsId}/comments")
    public Response<CommentResponse> comments(@PathVariable Long postsId, Authentication authentication, @RequestBody CommentRequest commentRequest) {

        CommentResponse commentResponse = postService.writeComment(postsId, authentication.getName(), commentRequest);
        return Response.success(commentResponse);

    }
    @ApiOperation(value = "댓글 목록 조회",notes = "특정 게시글에 달린 전체 댓글을 10개씩 조회 한다")
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentResponse>> commentsList(@PageableDefault(size = 10)
                                                            @SortDefault(sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable,@PathVariable Long postId) {
        Page<CommentResponse> commentsList =postService.getCommentsList(pageable,postId);
        return Response.success(commentsList);
    }

    @ApiOperation(value = "댓글 수정",notes = "기존 작성한 댓글을 수정 한다")
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentModifyResponse> commentModify(@PathVariable Long postId,@PathVariable Long id,
                                                   Authentication authentication,@RequestBody CommentRequest commentRequest) {
        CommentModifyResponse commentModifyResponse = postService.modifyComment(postId, id, authentication.getName(), commentRequest);
        return Response.success(commentModifyResponse);
    }

    @ApiOperation(value = "댓글 삭제",notes = "기존 작성한 댓글을 삭제 한다")
    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> commentDelete(@PathVariable Long postId,@PathVariable Long id,
                                                         Authentication authentication) {
        CommentDeleteResponse commentDeleteResponse = postService.deleteOneComment(postId,id,authentication.getName());
        return Response.success(commentDeleteResponse);
    }

    @ApiOperation(value = "좋아요",notes = "게시글에 좋아요를 추가 한다 (한번 더 호출할 경우 좋아요 취소)")
    @PostMapping("/{postId}/likes")
    public Response<String> likes(@PathVariable Long postId, Authentication authentication){
        String result = postService.pushLike(postId, authentication.getName());
        return Response.success(result);
    }

    @ApiOperation(value = "좋아요 개수",notes = "게시글에 달린 전체 좋아요 개수를 조회한다")
    @GetMapping("/{postId}/likes")
    public Response<Long> likes(@PathVariable Long postId){
        long count = postService.getCount(postId);
        return Response.success(new Long(count));
    }


}
