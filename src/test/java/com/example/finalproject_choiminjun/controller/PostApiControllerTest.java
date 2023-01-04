package com.example.finalproject_choiminjun.controller;

import com.example.finalproject_choiminjun.domain.Comment;
import com.example.finalproject_choiminjun.domain.dto.*;
import com.example.finalproject_choiminjun.exception.AppException;
import com.example.finalproject_choiminjun.exception.ErrorCode;
import com.example.finalproject_choiminjun.service.PostService;
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

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostApiController.class)
class PostApiControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PostService postService;

    @Test
    @DisplayName("포스트 작성 - 성공")
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
    @DisplayName("포스트 작성 - 인증 실패 #1 인증 되지 않은 유저")
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
    @DisplayName("포스트 1개 조회 - 성공")
    @WithMockUser
    void getOne_success() throws Exception {
        //given
        OnePostResponse onePostResponse = OnePostResponse.builder()
                .id(1L)
                .userName("userName")
                .title("title1")
                .body("content1")
                .lastModifiedAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .build();

        when(postService.get(any()))
                .thenReturn(onePostResponse);
        //when
        mockMvc.perform(get("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
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

    @Test
    @DisplayName("포스트 수정 - 성공")
    @WithMockUser
    void modify_success() throws Exception {

        PostRequest postRequest = new PostRequest("title_modify", "content_modify");

        PostResponse postResponse = new PostResponse("수정 성공", 1L);

        when(postService.modifyOnePost(any(), any(), any()))
                .thenReturn(postResponse);

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 수정 - 실패#1 인증 실패")
    @WithAnonymousUser
    void modify_fail1() throws Exception {

        PostRequest postRequest = new PostRequest("title_modify", "content_modify");

        PostResponse postResponse = new PostResponse("수정 성공", 1L);

        when(postService.modifyOnePost(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 - 실패#2 작성자 불일치")
    @WithMockUser
    void modify_fail2() throws Exception {

        PostRequest postRequest = new PostRequest("title_modify", "content_modify");

        PostResponse postResponse = new PostResponse("수정 성공", 1L);

        when(postService.modifyOnePost(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 수정 - 실패#3 게시글 없음")
    @WithMockUser
    void modify_fail3() throws Exception {

        PostRequest postRequest = new PostRequest("title_modify", "content_modify");

        PostResponse postResponse = new PostResponse("수정 성공", 1L);

        when(postService.modifyOnePost(any(), any(), any()))
                .thenThrow(new AppException(ErrorCode.POST_NOT_FOUND));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(postRequest))
                )
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("포스트 삭제 - 성공")
    @WithMockUser
    void post_delete_success() throws Exception {

        PostResponse postResponse = new PostResponse("포스트 삭제 완료", 1L);
        //given
        given(postService.deleteOnePost(any(), any()))
                .willReturn(postResponse);
        //when
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("포스트 삭제 - 실패 #1 인증 실패")
    @WithAnonymousUser
    void post_delete_fail_1() throws Exception {
        //given
        given(postService.deleteOnePost(any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        //when
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("포스트 삭제 - 실패 #2 작성자 불일치")
    @WithMockUser
    void post_delete_fail_2() throws Exception {

        given(postService.deleteOnePost(any(), any()))
                .willThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND));
        //when
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("포스트 삭제 - 실패 #3 데이터베이스 에러")
    @WithMockUser
    void post_delete_fail_3() throws Exception {

        given(postService.deleteOnePost(any(), any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));
        //when
        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("마이 피드 - 성공")
    @WithMockUser
    void my_feed_success() throws Exception {

        List<OnePostResponse> posts = new ArrayList<>();

        //given
        given(postService.myPost(any(), any()))
                .willReturn(Page.empty());
        //when
        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("마이 피드 - 실패 #1 로그인 하지 않는 경우")
    @WithAnonymousUser
    void my_feed_fail1() throws Exception {

        List<OnePostResponse> posts = new ArrayList<>();

        //given
        given(postService.myPost(any(), any()))
                .willReturn(Page.empty());
        //when
        mockMvc.perform(get("/api/v1/posts/my")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("댓글 작성 - 성공")
    @WithMockUser
    void comment_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        //given
        given(postService.writeComment(any(), any(), any()))
                .willReturn(new CommentResponse());

        //when
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest))
                )
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(status().isOk());
        //then
    }

    @Test
    @DisplayName("댓글 작성 - 실패#1 로그인 하지 않은 경우")
    @WithAnonymousUser
    void comment_fail1() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        //given
        given(postService.writeComment(any(), any(), any()))
                .willReturn(new CommentResponse());

        //when
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest))
                )
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then
    }

    @Test
    @DisplayName("댓글 작성 - 실패#2 게시물이 존재하지 않는 경우")
    @WithMockUser
    void comment_fail2() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test-1");
        //given
        given(postService.writeComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));

        //when
        mockMvc.perform(post("/api/v1/posts/1/comments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest))
                )
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isNotFound());
        //then
    }

    @Test
    @DisplayName("댓글 목록")
    @WithMockUser
    void commentList_success() throws Exception {

        List<CommentResponse> comments = new ArrayList<>();
        Page<CommentResponse> commentsList = new PageImpl<>(comments);;
        //given
        given(postService.getCommentsList(any(), any()))
                .willReturn(commentsList);
        //when
        mockMvc.perform(get("/api/v1/posts/4/comments")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
        //then

    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void comment_modify_success() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        CommentModifyResponse commentModifyResponse = CommentModifyResponse.builder()
                .comment("comment-test-modify")
                .build();
        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willReturn(commentModifyResponse);

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("SUCCESS"))
                .andExpect(status().isOk());
        //then

    }

    @Test
    @DisplayName("댓글 수정 실패#1 - 인증 실패")
    @WithAnonymousUser
    void comment_modify_fail1() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
        //then

    }

    @Test
    @DisplayName("댓글 수정 실패#2 - 댓글 불일치")
    @WithMockUser
    void comment_modify_fail2() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willThrow(new AppException(ErrorCode.COMMENTS_NOT_FOUND));

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isNotFound());
        //then

    }

    @Test
    @DisplayName("댓글 수정 실패#3 - 작성자 불일치")
    @WithMockUser
    void comment_modify_fail3() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isUnauthorized());
        //then

    }

    @Test
    @DisplayName("댓글 수정 실패#4 - 데이터베이스 에러")
    @WithMockUser
    void comment_modify_fail4() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isInternalServerError());
        //then

    }

    @Test
    @DisplayName("댓글 수정 실패#5 - Post 없는 경우")
    @WithMockUser
    void comment_modify_fail5() throws Exception {

        CommentRequest commentRequest = new CommentRequest("comment-test");

        //given
        given(postService.modifyComment(any(), any(), any(), any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));

        //when
        mockMvc.perform(put("/api/v1/posts/1/comments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(commentRequest)))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isNotFound());
        //then

    }

    @Test
    @DisplayName("댓글 삭제 - 성공")
    @WithMockUser
    void comments_delete_success() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willReturn(commentDeleteResponse);
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("result.message").value("삭제 완료"))
                .andExpect(status().isOk());

    }


    @Test
    @DisplayName("댓글 삭제 - 실패 #1 인증 실패")
    @WithAnonymousUser
    void comments_delete_fail1() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 #2 댓글 불일치")
    @WithMockUser
    void comments_delete_fail2() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.COMMENTS_NOT_FOUND));
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 #3 작성자 불일치")
    @WithMockUser
    void comments_delete_fail3() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.INVALID_PERMISSION));
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 #4 데이터베이스 에러")
    @WithMockUser
    void comments_delete_fail4() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.DATABASE_ERROR));
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("댓글 삭제 - 실패 #5 Post없는 경우")
    @WithMockUser
    void comments_delete_fail5() throws Exception {

        CommentDeleteResponse commentDeleteResponse = new CommentDeleteResponse("삭제 완료", 1L);

        //given
        given(postService.deleteOneComment(any(), any(), any()))
                .willThrow(new AppException(ErrorCode.POST_NOT_FOUND));
        //when
        mockMvc.perform(delete("/api/v1/posts/5/comments/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("resultCode").value("ERROR"))
                .andExpect(status().isNotFound());
    }

}
